package roy.interceptors;

import javax.annotation.security.PermitAll;
import javax.annotation.security.DenyAll;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import com.google.inject.Inject;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.util.Base64;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Logger;


// This is a sample authentication filter, based on information in the RESTEasy docs:
// https://docs.jboss.org/resteasy/docs/3.0.9.Final/userguide/html_single/
// http://howtodoinjava.com/resteasy/jax-rs-2-0-resteasy-3-0-2-final-security-tutorial/

@Provider
public class HTTPBasicAuthFilter implements javax.ws.rs.container.ContainerRequestFilter
{
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "Basic";
    private static final ServerResponse ACCESS_DENIED = new ServerResponse("Access denied for this resource", 401, new Headers<>());
    private static final ServerResponse ACCESS_FORBIDDEN = new ServerResponse("Nobody can access this resource", 403, new Headers<>());
    private static final ServerResponse SERVER_ERROR = new ServerResponse("INTERNAL SERVER ERROR", 500, new Headers<>());

    private final Logger logger;

    @Inject
    public HTTPBasicAuthFilter(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void filter(ContainerRequestContext requestContext)
    {
        ResourceMethodInvoker methodInvoker = (ResourceMethodInvoker) requestContext.getProperty("org.jboss.resteasy.core.ResourceMethodInvoker");
        Method method = methodInvoker.getMethod();

        if (method.isAnnotationPresent(PermitAll.class)) {
            logger.fine("Requested endpoint has @PermitAll set");
            return;
        }
        else if (method.isAnnotationPresent(DenyAll.class)) {
            logger.fine("Requested endpoint has @DenyAll set");
            requestContext.abortWith(ACCESS_FORBIDDEN);
            return;
        }

        // Check for an authorization header in the request
        MultivaluedMap<String, String> headers = requestContext.getHeaders();
        List<String> authorization = headers.get(AUTHORIZATION_HEADER_NAME);

        if (authorization == null || authorization.isEmpty()) {
            logger.severe("Request has no '" + AUTHORIZATION_HEADER_NAME + "' header");
            requestContext.abortWith(ACCESS_DENIED);
            return;
        }

        // Extract the userid and password
        String encodedUserIDPassword = authorization.get(0).replaceFirst(AUTHENTICATION_SCHEME + " ", "");
        String userID;
        String password;
        try {
            String userIDAndPassword = new String(Base64.decode(encodedUserIDPassword));
            String[] strings = userIDAndPassword.split(":");
            userID = strings[0];
            password = strings[1];
        } catch (IOException e) {
            logger.severe("Request '" + AUTHORIZATION_HEADER_NAME + "' header invalid; cannot extract userid and password");
            requestContext.abortWith(SERVER_ERROR);
            return;
        }

        logger.fine("Received request with userid=" + userID);

        // OBVIOUSLY YOU WOULD NEVER REALLY USE A HARD-CODED USERID/PASSWORD!
        if (!(userID.equals("roy") && password.equals("secret"))) {
            logger.severe("User " + userID + " authenticated/authorized rejected");
            requestContext.abortWith(ACCESS_DENIED);
        }

        // It's easy to pass along any useful data to the endpoint...
        requestContext.setProperty("auth-userid", userID);
        requestContext.setProperty("auth-password", password);

        logger.info("User " + userID + " authenticated/authorized");
    }
}


