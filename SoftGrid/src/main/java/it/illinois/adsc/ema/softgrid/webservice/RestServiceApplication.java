/* Copyright (C) 2016 Advanced Digital Science Centre

        * This file is part of Soft-Grid.
        * For more information visit https://www.illinois.adsc.com.sg/cybersage/
        *
        * Soft-Grid is free software: you can redistribute it and/or modify
        * it under the terms of the GNU General Public License as published by
        * the Free Software Foundation, either version 3 of the License, or
        * (at your option) any later version.
        *
        * Soft-Grid is distributed in the hope that it will be useful,
        * but WITHOUT ANY WARRANTY; without even the implied warranty of
        * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        * GNU General Public License for more details.
        *
        * You should have received a copy of the GNU General Public License
        * along with Soft-Grid.  If not, see <http://www.gnu.org/licenses/>.

        * @author Prageeth Mahendra Gunathilaka
*/
package it.illinois.adsc.ema.softgrid.webservice;

import it.illinois.adsc.ema.common.webservice.SecurityContext;
import it.illinois.adsc.ema.common.webservice.ServiceConfig;
import it.illinois.adsc.ema.softgrid.webservice.web.resources.FileRequest;
import it.illinois.adsc.ema.softgrid.webservice.web.resources.FileTransfer;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.Application;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;


/**
 * This is the Rest Service Application class which will initiate the service using a main method
 * NOTE : no need to deploy to a web server as this is a POC
 */
public class RestServiceApplication  extends Application {

    public RestServiceApplication() {
    }

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> s = new HashSet<Class<?>>();
        s.add(FileTransfer.class);
        s.add(FileRequest.class);
        s.add(SecurityContext.class);
        return s;
    }

    /**
     * Server start method
     * @return
     */
    public static HttpServer startServer() {
        final ResourceConfig rc = new ResourceConfig().packages("it.illinois.adsc.ema.webservice.web.resources");
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(ServiceConfig.BASE_URI), rc);
    }

    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at " +
                "%sapplication.wadl\nHit enter to stop it...", ServiceConfig.BASE_URI));
        System.in.read();
        server.stop();
    }
}

