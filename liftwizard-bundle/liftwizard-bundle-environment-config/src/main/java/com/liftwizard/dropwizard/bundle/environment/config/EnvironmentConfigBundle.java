package com.liftwizard.dropwizard.bundle.environment.config;

import io.dropwizard.ConfiguredBundle;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.MDC;
import org.slf4j.MDC.MDCCloseable;

public class EnvironmentConfigBundle
        implements ConfiguredBundle<Object>
{
    @Override
    public void initialize(Bootstrap<?> bootstrap)
    {
        try (MDCCloseable mdc = MDC.putCloseable("liftwizard.bundle", this.getClass().getSimpleName()))
        {
            this.initializeWithMdc(bootstrap);
        }
    }

    private void initializeWithMdc(Bootstrap<?> bootstrap)
    {
        ConfigurationSourceProvider configurationSourceProvider = bootstrap.getConfigurationSourceProvider();

        EnvironmentVariableSubstitutor environmentVariableSubstitutor = new EnvironmentVariableSubstitutor();
        environmentVariableSubstitutor.setPreserveEscapes(true);

        ConfigurationSourceProvider wrapped = new SubstitutingSourceProvider(
                configurationSourceProvider,
                environmentVariableSubstitutor);

        bootstrap.setConfigurationSourceProvider(wrapped);
    }

    @Override
    public void run(Object configuration, Environment environment)
    {
    }
}
