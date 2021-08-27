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

package io.liftwizard.dropwizard.bundle.config.logging;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auto.service.AutoService;
import io.dropwizard.setup.Environment;
import io.liftwizard.dropwizard.bundle.prioritized.PrioritizedBundle;
import io.liftwizard.dropwizard.configuration.config.logging.ConfigLoggingFactoryProvider;
import io.liftwizard.dropwizard.configuration.enabled.EnabledFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ConfigLoggingBundle logs the Dropwizard configuration to slf4j at INFO level, by serializing the in-memory configuration object to json.
 * @see <a href="https://liftwizard.io/docs/configuration/ConfigLoggingBundle#configloggingbundle">https://liftwizard.io/docs/configuration/ConfigLoggingBundle#configloggingbundle</a>
 */
@AutoService(PrioritizedBundle.class)
public class ConfigLoggingBundle
        implements PrioritizedBundle
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigLoggingBundle.class);

    @Override
    public int getPriority()
    {
        return -9;
    }

    @Override
    public void runWithMdc(@Nonnull Object configuration, @Nonnull Environment environment)
            throws JsonProcessingException, ReflectiveOperationException
    {
        ConfigLoggingFactoryProvider configLoggingFactoryProvider =
                this.safeCastConfiguration(ConfigLoggingFactoryProvider.class, configuration);

        EnabledFactory configLoggingFactory = configLoggingFactoryProvider.getConfigLoggingFactory();
        if (!configLoggingFactory.isEnabled())
        {
            LOGGER.info("{} disabled.", this.getClass().getSimpleName());
            return;
        }

        LOGGER.info("Running {}.", this.getClass().getSimpleName());

        ConfigLoggingBundle.logConfiguration(configuration, environment.getObjectMapper());

        LOGGER.info("Completing {}.", this.getClass().getSimpleName());
    }

    private static void logConfiguration(
            @Nonnull Object configuration,
            @Nonnull ObjectMapper objectMapper) throws JsonProcessingException, ReflectiveOperationException
    {
        String configurationString = objectMapper.writeValueAsString(configuration);
        LOGGER.info("Inferred Dropwizard configuration:\n{}", configurationString);

        Object defaultConfiguration       = configuration.getClass().getConstructor().newInstance();
        String defaultConfigurationString = objectMapper.writeValueAsString(defaultConfiguration);
        LOGGER.debug("Default Dropwizard configuration:\n{}", defaultConfigurationString);
    }
}
