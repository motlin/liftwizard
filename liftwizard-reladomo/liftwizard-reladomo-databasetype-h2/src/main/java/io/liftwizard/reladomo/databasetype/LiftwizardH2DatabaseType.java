/*
 * Copyright 2024 Craig Motlin
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

package io.liftwizard.reladomo.databasetype;

import java.sql.Connection;
import java.sql.SQLException;

import com.gs.fw.common.mithra.databasetype.H2DatabaseType;

public class LiftwizardH2DatabaseType
        extends H2DatabaseType
{
    private static final LiftwizardH2DatabaseType INSTANCE = new LiftwizardH2DatabaseType();

    public static LiftwizardH2DatabaseType getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void configureConnection(Connection connection)
            throws SQLException
    {
        this.fullyExecute(connection, "SET NON_KEYWORDS USER, KEY, VALUE");
    }
}
