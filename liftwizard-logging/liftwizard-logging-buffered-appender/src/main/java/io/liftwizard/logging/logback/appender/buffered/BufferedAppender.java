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

package io.liftwizard.logging.logback.appender.buffered;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Objects;
import java.util.Queue;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import ch.qos.logback.core.spi.DeferredProcessingAware;
import org.slf4j.Marker;

public class BufferedAppender<E extends DeferredProcessingAware>
        extends UnsynchronizedAppenderBase<E>
        implements AppenderAttachable<E>
{
    private final AppenderAttachableImpl<E> appenderAttachable = new AppenderAttachableImpl<>();
    private final Queue<E>                  queue              = new ArrayDeque<>();

    private int appenderCount;

    @Override
    public void start()
    {
        if (this.isStarted())
        {
            return;
        }

        if (this.appenderCount == 0)
        {
            this.addError("No attached appenders found.");
            return;
        }

        super.start();
    }

    @Override
    public void stop()
    {
        this.appenderAttachable.detachAndStopAllAppenders();
        super.stop();
    }

    @Override
    protected void append(E eventObject)
    {
        // preprocess for async
        eventObject.prepareForDeferredProcessing();
        if (eventObject instanceof ILoggingEvent)
        {
            ILoggingEvent loggingEvent = (ILoggingEvent) eventObject;

            loggingEvent.getCallerData();
            this.queue.add(eventObject);
            Marker marker = loggingEvent.getMarker();
            if (marker != null && Objects.equals(marker.getName(), "CLEAR"))
            {
                this.queue.clear();
            }
            else if (marker != null && Objects.equals(marker.getName(), "FLUSH"))
            {
                while (!this.queue.isEmpty())
                {
                    this.appenderAttachable.appendLoopOnAppenders(this.queue.remove());
                }
            }
        }
    }

    @Override
    public void addAppender(Appender<E> newAppender)
    {
        if (this.appenderCount == 0)
        {
            this.appenderCount++;
            this.addInfo("Attaching appender named [" + newAppender.getName() + "] to BufferedAppender.");
            this.appenderAttachable.addAppender(newAppender);
        }
        else
        {
            this.addWarn("One and only one appender may be attached to BufferedAppender.");
            this.addWarn("Ignoring additional appender named [" + newAppender.getName() + "]");
        }
    }

    @Override
    public Iterator<Appender<E>> iteratorForAppenders()
    {
        return this.appenderAttachable.iteratorForAppenders();
    }

    @Override
    public Appender<E> getAppender(String name)
    {
        return this.appenderAttachable.getAppender(name);
    }

    @Override
    public boolean isAttached(Appender<E> appender)
    {
        return this.appenderAttachable.isAttached(appender);
    }

    @Override
    public void detachAndStopAllAppenders()
    {
        this.appenderAttachable.detachAndStopAllAppenders();
    }

    @Override
    public boolean detachAppender(Appender<E> appender)
    {
        return this.appenderAttachable.detachAppender(appender);
    }

    @Override
    public boolean detachAppender(String name)
    {
        return this.appenderAttachable.detachAppender(name);
    }
}
