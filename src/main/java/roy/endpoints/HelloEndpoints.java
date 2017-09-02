package roy.endpoints;

import com.google.inject.Inject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;

@Path("/HelloRESTEasy")
public class HelloEndpoints {
    private final Logger logger;

    @Inject
    public HelloEndpoints(Logger logger) {
        this.logger = logger;
    }

    // curl -H "Accept: application/json" http://localhost:8080/hello_resteasy/HelloRESTEasy/hello/roy ; echo
    @GET
    @Path("/hello/{userid}")
    @Produces({MediaType.APPLICATION_JSON})
    public String getHello(@PathParam("userid") String userid) {
        logger.info("Received request with userid=" + userid);

        String response = "Hello, " + userid + ".\r\n";

        return response;
    }

}