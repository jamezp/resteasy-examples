/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2025 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
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

package dev.resteasy.examples.service;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@Path("/sse")
@RequestScoped
public class NdResource {

    @GET
    @Path("/ndjson/{count}")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void produce(@Context final SseEventSink sseEventSink, @Context final Sse sse, @PathParam("count") final int count) {
        for (int i = 0; i < count; i++) {
            StringBuilder value = new StringBuilder();
            for (int j = 0; j < 10; j++) {
                value.append(createJson(i, j)).append('\n');
            }
            final var event = sse.newEventBuilder()
                    .data(value.toString())
                    .mediaType(MediaType.valueOf("application/ndjson"))
                    .build();
            sseEventSink.send(event);
        }
    }

    private String createJson(final int i, final int j) {
        return "{\"name\":\"" + i + " - " + j + "\"}";
    }
}
