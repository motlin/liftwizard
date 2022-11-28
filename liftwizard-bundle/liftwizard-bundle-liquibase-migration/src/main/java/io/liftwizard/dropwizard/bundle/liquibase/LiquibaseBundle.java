/*
 * Copyright 2022 Craig Motlin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.liftwizard.dropwizard.bundle.liquibase;

import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.auto.service.AutoService;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.setup.Environment;
import io.liftwizard.dropwizard.bundle.prioritized.PrioritizedBundle;
import io.liftwizard.dropwizard.configuration.datasource.NamedDataSourceProvider;
import io.liftwizard.dropwizard.configuration.liquibase.migration.LiquibaseMigrationFactory;
import io.liftwizard.dropwizard.configuration.liquibase.migration.LiquibaseMigrationFactoryProvider;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AutoService(PrioritizedBundle.class)
public class LiquibaseBundle
        implements PrioritizedBundle
{
    private static final Logger LOGGER = LoggerFactory.getLogger(LiquibaseBundle.class);

    @Override
    public int getPriority()
    {
        return -6;
    }

    @Override
    public void runWithMdc(@Nonnull Object configuration, @Nonnull Environment environment)
    {
        LiquibaseMigrationFactoryProvider liquibaseFactoryProvider = this.safeCastConfiguration(
                LiquibaseMigrationFactoryProvider.class,
                configuration);

        if (liquibaseFactoryProvider == null)
        {
            LOGGER.info("{} disabled.", this.getClass().getSimpleName());
            return;
        }

        LOGGER.info("Running {}.", this.getClass().getSimpleName());

        List<LiquibaseMigrationFactory> liquibaseMigrationFactories = liquibaseFactoryProvider.getLiquibaseMigrationFactories();

        NamedDataSourceProvider dataSourceProvider = this.safeCastConfiguration(
                NamedDataSourceProvider.class,
                configuration);

        for (LiquibaseMigrationFactory liquibaseMigrationFactory : liquibaseMigrationFactories)
        {
            String            dataSourceName = liquibaseMigrationFactory.getDataSourceName();
            ManagedDataSource dataSource     = dataSourceProvider.getDataSourceByName(dataSourceName);

            boolean dryRun = liquibaseFactoryProvider.isDryRun();

            String       catalogName   = liquibaseMigrationFactory.getCatalogName();
            String       schemaName    = liquibaseMigrationFactory.getSchemaName();
            String       migrationFile = liquibaseMigrationFactory.getMigrationFile();
            List<String> contexts      = liquibaseMigrationFactory.getContexts();
            String       context       = String.join(",", contexts);

            try (
                    AbstractCloseableLiquibase liquibase = this.openLiquibase(
                            dataSource,
                            catalogName,
                            schemaName,
                            migrationFile))
            {
                if (dryRun)
                {
                    liquibase.update(context, new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
                }
                else
                {
                    liquibase.update(context);
                }
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }

        LOGGER.info("Completing {}.", this.getClass().getSimpleName());
    }

    private AbstractCloseableLiquibase openLiquibase(
            ManagedDataSource dataSource,
            String catalogName,
            String schemaName,
            String migrationsFile)
            throws SQLException, LiquibaseException
    {
        Database database = this.createDatabase(dataSource, catalogName, schemaName);

        return new CloseableLiquibaseWithFileSystemMigrationsFile(dataSource, database, migrationsFile);
    }

    private Database createDatabase(ManagedDataSource dataSource, String catalogName, String schemaName)
            throws SQLException, LiquibaseException
    {
        DatabaseConnection conn     = new JdbcConnection(dataSource.getConnection());
        Database           database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(conn);

        if (database.supportsCatalogs() && catalogName != null)
        {
            database.setDefaultCatalogName(catalogName);
            database.setOutputDefaultCatalog(true);
        }
        if (database.supportsSchemas() && schemaName != null)
        {
            database.setDefaultSchemaName(schemaName);
            database.setOutputDefaultSchema(true);
        }

        return database;
    }
}
