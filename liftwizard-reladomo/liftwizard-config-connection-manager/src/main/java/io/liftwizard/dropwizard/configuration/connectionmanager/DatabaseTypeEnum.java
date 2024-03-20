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

package io.liftwizard.dropwizard.configuration.connectionmanager;

import java.util.Objects;

import com.gs.fw.common.mithra.databasetype.DatabaseType;
import com.gs.fw.common.mithra.databasetype.DerbyDatabaseType;
import com.gs.fw.common.mithra.databasetype.GenericDatabaseType;
import com.gs.fw.common.mithra.databasetype.MariaDatabaseType;
import com.gs.fw.common.mithra.databasetype.MsSqlDatabaseType;
import com.gs.fw.common.mithra.databasetype.OracleDatabaseType;
import com.gs.fw.common.mithra.databasetype.PostgresDatabaseType;
import com.gs.fw.common.mithra.databasetype.SybaseDatabaseType;
import com.gs.fw.common.mithra.databasetype.SybaseIqDatabaseType;
import com.gs.fw.common.mithra.databasetype.Udb82DatabaseType;
import io.liftwizard.reladomo.databasetype.LiftwizardH2DatabaseType;

public enum DatabaseTypeEnum
{
    DERBY(DerbyDatabaseType.getInstance()),
    GENERIC(GenericDatabaseType.getInstance()),
    H2(LiftwizardH2DatabaseType.getInstance()),
    MARIA(MariaDatabaseType.getInstance()),
    MSSQL(MsSqlDatabaseType.getInstance()),
    ORACLE(OracleDatabaseType.getInstance()),
    POSTGRES(PostgresDatabaseType.getInstance()),
    SYBASE(SybaseDatabaseType.getInstance()),
    SYBASE_IQ(SybaseIqDatabaseType.getInstance()),
    SYBASE_IQ_NATIVE(SybaseIqDatabaseType.getInstance()),
    UDB_82(Udb82DatabaseType.getInstance()),;

    private final DatabaseType databaseType;

    DatabaseTypeEnum(DatabaseType databaseType)
    {
        this.databaseType = Objects.requireNonNull(databaseType);
    }

    public DatabaseType getDatabaseType()
    {
        return this.databaseType;
    }
}
