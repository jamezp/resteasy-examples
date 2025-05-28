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

/**
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@Stateless
@Local({GreetEjb.class, GoodbyeEjb.class})
@Path("/")
public class LocalEjbImpl implements GreetEjb, GoodbyeEjb {

    @Resource
    SessionContext ctx;

    @Override
    @GET
    @Path("hello")
    public String hello() {
        if (ctx == null) {
            return "ctx is null";
        }
        return "Hello, World!";
    }

    @Override
    @GET
    @Path("goodbye")
    public String goodbye() {
        if (ctx == null) {
            return "ctx is null";
        }
        return "Goodbye, World!";
    }
}
