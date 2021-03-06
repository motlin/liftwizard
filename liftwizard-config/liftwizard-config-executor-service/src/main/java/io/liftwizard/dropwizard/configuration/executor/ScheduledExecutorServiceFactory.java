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

package io.liftwizard.dropwizard.configuration.executor;

import java.util.concurrent.TimeUnit;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.lifecycle.setup.ScheduledExecutorServiceBuilder;
import io.dropwizard.setup.Environment;
import io.dropwizard.util.Duration;
import io.dropwizard.validation.MinDuration;

public class ScheduledExecutorServiceFactory
{
    @Valid
    @NotEmpty
    private String   nameFormat;
    @Min(0)
    private int      threads      = 1;
    @NotNull
    @MinDuration(value = 0, unit = TimeUnit.MILLISECONDS, inclusive = false)
    private Duration shutdownTime = Duration.seconds(5);
    private boolean  removeOnCancelPolicy;

    @JsonIgnore
    public ScheduledExecutorServiceBuilder build(Environment environment)
    {
        return this.build(environment.lifecycle());
    }

    @JsonIgnore
    public ScheduledExecutorServiceBuilder build(LifecycleEnvironment environment)
    {
        return environment
                .scheduledExecutorService(this.nameFormat)
                .threads(this.threads)
                .shutdownTime(this.shutdownTime)
                .removeOnCancelPolicy(this.removeOnCancelPolicy);
    }

    @JsonProperty
    public String getNameFormat()
    {
        return this.nameFormat;
    }

    @JsonProperty
    public void setNameFormat(String nameFormat)
    {
        this.nameFormat = nameFormat;
    }

    @JsonProperty
    public int getThreads()
    {
        return this.threads;
    }

    @JsonProperty
    public void setThreads(int threads)
    {
        this.threads = threads;
    }

    @JsonProperty
    public Duration getShutdownTime()
    {
        return this.shutdownTime;
    }

    @JsonProperty
    public void setShutdownTime(Duration shutdownTime)
    {
        this.shutdownTime = shutdownTime;
    }

    @JsonProperty
    public boolean isRemoveOnCancelPolicy()
    {
        return this.removeOnCancelPolicy;
    }

    @JsonProperty
    public void setRemoveOnCancelPolicy(boolean removeOnCancelPolicy)
    {
        this.removeOnCancelPolicy = removeOnCancelPolicy;
    }
}
