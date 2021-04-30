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

package io.liftwizard.dropwizard.bundle.httplogging;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import com.google.auto.service.AutoService;
import io.dropwizard.setup.Environment;
import io.liftwizard.dropwizard.bundle.prioritized.PrioritizedBundle;
import io.liftwizard.dropwizard.configuration.http.logging.JerseyHttpLoggingFactory;
import io.liftwizard.dropwizard.configuration.http.logging.JerseyHttpLoggingFactoryProvider;
import io.liftwizard.logging.jersey.LoggingFeature;
import io.liftwizard.logging.jersey.LoggingFeature.Verbosity;
import org.slf4j.LoggerFactory;

/**
 * Logs all requests and responses to slf4j. The verbosity and maxEntitySize are configurable.
 *
 * @see <a href="https://liftwizard.io/docs/logging/JerseyHttpLoggingBundle#jerseyhttploggingbundle">https://liftwizard.io/docs/logging/JerseyHttpLoggingBundle#jerseyhttploggingbundle</a>
 */
@AutoService(PrioritizedBundle.class)
public class JerseyHttpLoggingBundle
        implements PrioritizedBundle<Object>
{
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(JerseyHttpLoggingBundle.class);

    @Override
    public void runWithMdc(@Nonnull Object configuration, @Nonnull Environment environment)
    {
        JerseyHttpLoggingFactoryProvider jerseyHttpLoggingFactoryProvider = this.safeCastConfiguration(
                JerseyHttpLoggingFactoryProvider.class,
                configuration);

        JerseyHttpLoggingFactory factory = jerseyHttpLoggingFactoryProvider.getJerseyHttpLoggingFactory();
        if (!factory.isEnabled())
        {
            LOGGER.info("{} disabled.", this.getClass().getSimpleName());
            return;
        }

        LOGGER.info("Running {}.", this.getClass().getSimpleName());

        Level     level         = Level.parse(factory.getLevel());
        Verbosity verbosity     = Verbosity.valueOf(factory.getVerbosity());
        int       maxEntitySize = Math.toIntExact(factory.getMaxEntitySize().toBytes());

        Logger         logger         = Logger.getLogger(JerseyHttpLoggingBundle.class.getName());
        LoggingFeature loggingFeature = new LoggingFeature(logger, level, verbosity, maxEntitySize);
        environment.jersey().register(loggingFeature);

        LOGGER.info("Completing {}.", this.getClass().getSimpleName());
    }
}
