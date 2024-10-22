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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * A simple resource for creating a greeting.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@Path("/")
public class UploadResource {

    @POST
    @Path("upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response upload(final List<EntityPart> entityParts) {
        final JsonObjectBuilder builder = Json.createObjectBuilder();
        for (EntityPart entityPart : entityParts) {
            builder.add("name", entityPart.getName());
            if (entityPart.getFileName().isPresent()) {
                builder.add("filename", entityPart.getFileName().get());
                final java.nio.file.Path download = java.nio.file.Path
                        .of(System.getProperty("java.io.tmpdir"), entityPart.getFileName().get());
                try (InputStream in = entityPart.getContent()) {
                    if (in != null) {
                        Files.copy(in, download, StandardCopyOption.REPLACE_EXISTING);
                        builder.add("content", download.toString());
                    } else {
                        builder.addNull("content");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                builder.addNull("filename");
                builder.addNull("content");
            }
        }
        return Response.ok(builder.build()).build();
    }
}
