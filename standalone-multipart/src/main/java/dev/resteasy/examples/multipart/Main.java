/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2023 Red Hat, Inc., and individual contributors
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

package dev.resteasy.examples.multipart;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import jakarta.ws.rs.SeBootstrap;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * An entry point for starting a REST container
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class Main {

    public static void main(final String[] args) throws Exception {
        System.setProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager");

        // Start the container
        final SeBootstrap.Instance instance = SeBootstrap.start(RestActivator.class)
                .thenApply(i -> {
                    System.out.printf("Container running at %s%n", i.configuration().baseUri());
                    return i;
                }).toCompletableFuture().get();

        Path png = null;
        if (args != null && args.length > 0) {
            png = Path.of(args[0]);
            if (Files.notExists(png)) {
                System.err.println("Using " + png);
            }
        } else {
            System.err.println("A file must be specified");
            System.exit(1);
        }

        // Create the client and make a multipart/form-data request
        try (Client client = ClientBuilder.newClient()) {
            // Create the entity parts for the request
            final List<EntityPart> multipart = List.of(
                    EntityPart.withName("imagem")
                            .content(Files.readAllBytes(png))
                            .fileName(png.getFileName().toString())
                            .mediaType(MediaType.APPLICATION_OCTET_STREAM)
                            .build());
            try (
                    Response response = client.target(instance.configuration().baseUriBuilder().path("/api/upload"))
                            .request(MediaType.APPLICATION_JSON_TYPE)
                            .post(Entity.entity(new GenericEntity<>(multipart) {
                            }, MediaType.MULTIPART_FORM_DATA_TYPE))) {
                printResponse(response);
            }
        }
    }

    private static void printResponse(final Response response) {
        System.out.println(response.getStatusInfo());
        System.out.println(response.readEntity(String.class));
    }

}
