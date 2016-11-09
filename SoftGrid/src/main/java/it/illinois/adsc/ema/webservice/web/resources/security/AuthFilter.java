package it.illinois.adsc.ema.webservice.web.resources.security;


import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.container.PreMatching;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * This class filter out all the unauthorized access to the service
 */
@PreMatching
@Provider
public class AuthFilter implements ContainerRequestFilter {
    private static Properties credentials = new Properties();

    public AuthFilter() {
        if (credentials != null) {
            try {
                credentials.load(getClass().getClassLoader().getResourceAsStream("password.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Filter the authorized access and asign user roles
     *
     * @param request user request
     * @throws WebApplicationException
     */
    @Override
    public void filter(ContainerRequestContext request) throws WebApplicationException {
        String path = request.getUriInfo().getPath(true);
        //No authentication is needed to obtain the wadl or the wadl xsd
        if (path.equals("application.wadl") || path.equals("application.wadl/xsd0.xsd")) {
            return;
        }
        String auth = request.getHeaderString("auth");
        String[] userPassword;
        //If the user does not have the right (does not provide any HTTP Basic Auth)
        if (auth == null) {
            throw new WebApplicationException(Status.UNAUTHORIZED);
        } else {
            userPassword = decode(auth);
            if (userPassword == null || userPassword.length != 2 ||
                    userPassword[0].trim().isEmpty() || userPassword[1].trim().isEmpty()) {
                throw new WebApplicationException(Status.UNAUTHORIZED);
            }
        }
        // create a user object to add to the security context
        // todo Need to read the user credential database or a password file to validate this
        if (!credentials.containsKey(userPassword[0]) && credentials.getProperty(userPassword[0]).equals(userPassword[1])) {
            throw new WebApplicationException(Status.UNAUTHORIZED);
        }
        List<String> usernameList = new ArrayList<String>();
        usernameList.add(userPassword[0]);
        request.getHeaders().put("username", usernameList);

    }

    public static String[] decode(String auth) {
        auth = auth.replaceFirst("[B|b]asic ", "");
        //Decode the Base64 into byte[]
        byte[] decodedBytes = DatatypeConverter.parseBase64Binary(auth);
        if (decodedBytes == null || decodedBytes.length == 0) {
            return null;
        } else {
            // split the string by : to seperate the username and password
            return new String(decodedBytes).split(":", 2);
        }
    }
}