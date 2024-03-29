/*
 * Copyright 2021 Craig Motlin
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

import java.util.TimeZone;

import javax.validation.Validator;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gs.fw.common.mithra.connectionmanager.SourcelessConnectionManager;
import com.gs.fw.common.mithra.databasetype.GenericDatabaseType;
import io.dropwizard.configuration.JsonConfigurationFactory;
import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.liftwizard.junit.rule.log.marker.LogMarkerTestRule;
import io.liftwizard.serialization.jackson.config.ObjectMapperConfig;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ConnectionManagerFactoryTest
{
    @Rule
    public final TestRule logMarkerTestRule = new LogMarkerTestRule();

    private final ObjectMapper objectMapper = newObjectMapper();
    private final Validator    validator    = Validators.newValidator();

    private final JsonConfigurationFactory<ConnectionManagerFactory> factory =
            new JsonConfigurationFactory<>(ConnectionManagerFactory.class, this.validator, this.objectMapper, "dw");

    @Test
    public void createSourcelessConnectionManager() throws Exception
    {
        ConnectionManagerFactory connectionManagerFactory = this.factory.build(
                new ResourceConfigurationSourceProvider(),
                "config-test.json5");

        DataSourceFactory dataSourceFactory = new DataSourceFactory();
        ManagedDataSource managedDataSource = dataSourceFactory.build(new MetricRegistry(), "test");

        SourcelessConnectionManager sourcelessConnectionManager =
                connectionManagerFactory.createSourcelessConnectionManager(managedDataSource);

        assertThat(sourcelessConnectionManager.getDatabaseIdentifier(), is("schemaName"));
        assertThat(sourcelessConnectionManager.getDatabaseTimeZone(), is(TimeZone.getTimeZone("America/New_York")));
        assertThat(sourcelessConnectionManager.getDatabaseType(), is(GenericDatabaseType.getInstance()));
    }

    private static ObjectMapper newObjectMapper()
    {
        ObjectMapper objectMapper = Jackson.newObjectMapper();
        ObjectMapperConfig.configure(objectMapper);
        return objectMapper;
    }
}
