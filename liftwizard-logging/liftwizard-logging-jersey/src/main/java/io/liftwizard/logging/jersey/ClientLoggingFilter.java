/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package io.liftwizard.logging.jersey;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Priority;
import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.FeatureContext;

import io.liftwizard.logging.jersey.LoggingFeature.Verbosity;
import org.glassfish.jersey.message.MessageUtils;

/**
 * Client filter logs requests and responses to specified logger, at required level, with entity or not.
 *
 * <p>
 * The filter is registered in {@link LoggingFeature#configure(FeatureContext)} and can be used on client side only. The priority
 * is set to the minimum value, which means that filter is called as the last filter when request is sent and similarly as the
 * first filter when the response is received, so request and response is logged as sent or as received.
 *
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 * @author Martin Matula
 * @author Ondrej Kosatka (ondrej.kosatka at oracle.com)
 */
@ConstrainedTo(RuntimeType.CLIENT)
@PreMatching
@Priority(Integer.MAX_VALUE)
public final class ClientLoggingFilter
        extends AbstractLoggingInterceptor
        implements ClientRequestFilter, ClientResponseFilter
{
    /**
     * Create a logging filter with custom logger and custom settings of entity
     * logging.
     *
     * @param logger        the logger to log messages to.
     * @param level         level at which the messages will be logged.
     * @param verbosity     verbosity of the logged messages. See {@link Verbosity}.
     * @param maxEntitySize maximum number of entity bytes to be logged (and buffered) - if the entity is larger,
     *                      logging filter will print (and buffer in memory) only the specified number of bytes
     *                      and print "...more..." string at the end. Negative values are interpreted as zero.
     */
    public ClientLoggingFilter(
            Logger logger,
            Level level,
            Verbosity verbosity,
            int maxEntitySize)
    {
        super(logger, level, verbosity, maxEntitySize);
    }

    @Override
    public void filter(ClientRequestContext requestContext)
    {
        if (!this.logger.isLoggable(this.level))
        {
            return;
        }
        long id = this.idCounter.incrementAndGet();
        requestContext.setProperty(LOGGING_ID_PROPERTY, id);

        StringBuilder stringBuilder = new StringBuilder();

        this.printRequestLine(stringBuilder, "Sending client request", id, requestContext.getMethod(), requestContext.getUri());
        this.printPrefixedHeaders(stringBuilder, id, REQUEST_PREFIX, requestContext.getStringHeaders());

        if (requestContext.hasEntity() && AbstractLoggingInterceptor.printEntity(this.verbosity, requestContext.getMediaType()))
        {
            OutputStream stream = new LoggingStream(stringBuilder, requestContext.getEntityStream());
            requestContext.setEntityStream(stream);
            requestContext.setProperty(ENTITY_LOGGER_PROPERTY, stream);
            // not calling log(stringBuilder) here - it will be called by the interceptor
        }
        else
        {
            this.log(stringBuilder);
        }
    }

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext)
            throws IOException
    {
        if (!this.logger.isLoggable(this.level))
        {
            return;
        }
        Object requestId = requestContext.getProperty(LOGGING_ID_PROPERTY);
        long   id        = requestId != null ? (Long) requestId : this.idCounter.incrementAndGet();

        StringBuilder b = new StringBuilder();

        this.printResponseLine(b, "Client response received", id, responseContext.getStatus());
        this.printPrefixedHeaders(b, id, RESPONSE_PREFIX, responseContext.getHeaders());

        if (responseContext.hasEntity() && AbstractLoggingInterceptor.printEntity(this.verbosity, responseContext.getMediaType()))
        {
            responseContext.setEntityStream(this.logInboundEntity(b, responseContext.getEntityStream(),
                    MessageUtils.getCharset(responseContext.getMediaType())));
        }

        this.log(b);
    }
}
