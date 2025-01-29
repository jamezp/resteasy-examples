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

package dev.resteasy.examples;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.sse.SseEventSource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import dev.resteasy.examples.providers.NdJsonProvider;
import dev.resteasy.examples.service.LibraryApplication;
import dev.resteasy.examples.service.NdResource;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@ArquillianTest
@RunAsClient
public class NdJsonProviderTest {

    @ArquillianResource
    private URI base;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(NdJsonProvider.class, NdResource.class, LibraryApplication.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void ndJson() throws Exception {
        final int count = 10;
        final CountDownLatch latch = new CountDownLatch(count);
        try (Client client = ClientBuilder.newClient()) {
            client.register(NdJsonProvider.class);
            final List<List<JsonObject>> items = new ArrayList<>();
            try (SseEventSource sseEventSource = SseEventSource
                    .target(client.target(UriBuilder.fromUri(base).path("sse/ndjson/" + count))).build()) {
                sseEventSource.register((event) -> {
                    try {
                        items.add(event.readData(new GenericType<>() {
                        }, MediaType.valueOf("application/ndjson")));
                    } catch (Throwable e) {
                        e.printStackTrace();
                        drainLatch(latch);
                    }
                    latch.countDown();
                }, (error) -> {
                    error.printStackTrace();
                    drainLatch(latch);
                });
                sseEventSource.open();
                Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
                Assertions.assertEquals(count, items.size());
                Assertions.assertEquals(10, items.get(0).size());
            }
        }
    }

    private static void drainLatch(final CountDownLatch latch) {
        while (latch.getCount() > 0) {
            latch.countDown();
        }
    }
}
