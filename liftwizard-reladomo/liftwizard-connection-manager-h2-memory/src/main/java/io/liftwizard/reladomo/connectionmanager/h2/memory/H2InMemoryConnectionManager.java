/*
 * Copyright 2020 Craig Motlin
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

package io.liftwizard.reladomo.connectionmanager.h2.memory;

import java.sql.Connection;
import java.util.TimeZone;

import javax.annotation.Nonnull;

import com.gs.fw.common.mithra.bulkloader.BulkLoader;
import com.gs.fw.common.mithra.connectionmanager.SourcelessConnectionManager;
import com.gs.fw.common.mithra.connectionmanager.XAConnectionManager;
import com.gs.fw.common.mithra.databasetype.DatabaseType;
import io.liftwizard.reladomo.databasetype.LiftwizardH2DatabaseType;

public final class H2InMemoryConnectionManager
        implements SourcelessConnectionManager
{
    private static final H2InMemoryConnectionManager INSTANCE = new H2InMemoryConnectionManager();

    private static final TimeZone            TIME_ZONE             = TimeZone.getTimeZone("UTC");
    private static final String              SCHEMA_NAME           = "liftwizard-app-h2";
    private static final XAConnectionManager XA_CONNECTION_MANAGER =
            H2InMemoryConnectionManager.createXaConnectionManager();

    private H2InMemoryConnectionManager()
    {
        // singleton
    }

    @Nonnull
    private static XAConnectionManager createXaConnectionManager()
    {
        // TODO: Consider using org.apache.tomcat.jdbc.pool.DataSourceProxy and org.apache.tomcat.jdbc.pool.PoolProperties instead
        XAConnectionManager connectionManager = new XAConnectionManager();
        connectionManager.setDatabaseType(LiftwizardH2DatabaseType.getInstance());
        connectionManager.setDriverClassName("com.p6spy.engine.spy.P6SpyDriver");
        connectionManager.setMaxWait(500);
        connectionManager.setJdbcConnectionString("jdbc:p6spy:h2:mem:");
        connectionManager.setJdbcUser("sa");
        connectionManager.setJdbcPassword("");
        connectionManager.setPoolName("Reladomo default connection pool");
        connectionManager.setInitialSize(1);
        connectionManager.setPoolSize(10);
        connectionManager.initialisePool();
        return connectionManager;
    }

    @Nonnull
    @SuppressWarnings("unused")
    public static H2InMemoryConnectionManager getInstance()
    {
        return INSTANCE;
    }

    @Nonnull
    @Override
    public BulkLoader createBulkLoader()
    {
        throw new RuntimeException("BulkLoader is not supported");
    }

    @Override
    public Connection getConnection()
    {
        return XA_CONNECTION_MANAGER.getConnection();
    }

    @Override
    public DatabaseType getDatabaseType()
    {
        return LiftwizardH2DatabaseType.getInstance();
    }

    @Override
    public TimeZone getDatabaseTimeZone()
    {
        return TIME_ZONE;
    }

    @Override
    public String getDatabaseIdentifier()
    {
        return SCHEMA_NAME;
    }
}
