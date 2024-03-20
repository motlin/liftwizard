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

package io.liftwizard.reladomo.connectionmanager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.TimeZone;

import javax.sql.DataSource;

import com.gs.fw.common.mithra.bulkloader.BulkLoader;
import com.gs.fw.common.mithra.connectionmanager.SourcelessConnectionManager;
import com.gs.fw.common.mithra.databasetype.DatabaseType;

public class LiftwizardConnectionManager implements SourcelessConnectionManager
{
    private final String       connectionManagerName;
    private final String       dataSourceName;
    private final DataSource   dataSource;
    private final DatabaseType databaseType;
    private final TimeZone     databaseTimeZone;
    private final String       schemaName;

    public LiftwizardConnectionManager(
            String connectionManagerName,
            String dataSourceName,
            DataSource dataSource,
            DatabaseType databaseType,
            TimeZone databaseTimeZone,
            String schemaName)
    {
        this.connectionManagerName = Objects.requireNonNull(connectionManagerName);
        this.dataSourceName        = Objects.requireNonNull(dataSourceName);
        this.dataSource            = Objects.requireNonNull(dataSource);
        this.databaseType          = Objects.requireNonNull(databaseType);
        this.databaseTimeZone      = Objects.requireNonNull(databaseTimeZone);
        this.schemaName            = Objects.requireNonNull(schemaName);
    }

    public String getConnectionManagerName()
    {
        return this.connectionManagerName;
    }

    public String getDataSourceName()
    {
        return this.dataSourceName;
    }

    @Override
    public BulkLoader createBulkLoader()
    {
        throw new RuntimeException("BulkLoader is not supported");
    }

    @Override
    public Connection getConnection()
    {
        try
        {
            Connection connection = this.dataSource.getConnection();
            this.databaseType.configureConnection(connection);
            return connection;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DatabaseType getDatabaseType()
    {
        return this.databaseType;
    }

    @Override
    public TimeZone getDatabaseTimeZone()
    {
        return this.databaseTimeZone;
    }

    @Override
    public String getDatabaseIdentifier()
    {
        return this.schemaName;
    }
}
