package it.illinois.adsc.ema.webservice;

import it.illinois.adsc.ema.common.webservice.SecurityContext;
import it.illinois.adsc.ema.common.webservice.ServiceConfig;
import it.illinois.adsc.ema.webservice.web.resources.FileRequest;
import it.illinois.adsc.ema.webservice.web.resources.FileTransfer;
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

