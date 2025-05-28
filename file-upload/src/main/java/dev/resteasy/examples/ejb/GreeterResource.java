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

package dev.resteasy.examples.ejb;

import jakarta.annotation.Resource;
import jakarta.ejb.Local;
import jakarta.ejb.SessionContext;
import jakarta.ejb.Stateless;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@Local(Greeter.class)
@Stateless
@Path("/greet")
public class GreeterResource implements Greeter {

    @Resource
    private SessionContext sessionContext;

    @GET
    @Path("{name}")
    @Override
    public String greet(@PathParam("name") final String name) {
        return String.format("Hello, %s (%s)!", name, sessionContext.getCallerPrincipal().getName());
    }
}
