# HelloRESTEasy

This is a simple project to demonstrate how to create a RESTEasy service.

The project relies heavily on maven, so pay attention to the pom.xml file.  In particular, the project/build/finalName element specifies the name of the .war file that will be generated, and this in turn determines the servlet context name that Tomcat will use.

To build the project, run "mvn package" from the command line at the root of the project.

Once the project is built (i.e. the hello_resteasy.war file is created in the target folder), you can deploy it to Tomcat via the manager GUI or via maven.

If you are deploying to Tomcat using the maven plugin, make sure you edit your ~/.m2/settings.xml file to include the credentials for your Tomcat installation.  For example:

    <?xml version="1.0" encoding="UTF-8"?>
    <settings>
        <servers>
            <server>
                <id>TomcatServer</id>
                <username>admin</username>
                <password>admin</password>
            </server>
        </servers>
    </settings>

Note that the Tomcat credentials you specify in ~/.m2/settings.xml must also match the credentials in your Tomcat <CATALINA_HOME>/conf/tomcat-users.xml file.  For example:

    <?xml version="1.0" encoding="UTF-8"?>
    <tomcat-users>
        <user username="admin" password="admin" roles="manager-gui,manager-script" />
    </tomcat-users>

To deploy via maven, execute "mvn tomcat7:deploy" or "mvn tomcat7:redeploy" from the command line at the root of the project.

 
The project uses Google Guice for dependency injection.  For example, the Logger is automatically injected when an instance of the HelloEndpoints class is created by RESTEasy.  For this to work correctly, you have to register your own classes, as is done in the GuiceModule class.  Also note that the GuiceModule is registered with RESTEasy in the web.xml configuration.

Once the service is deployed, you can invoke services using curl like this:

    curl -H "Accept: application/json" http://localhost:8080/hello_resteasy/HelloRESTEasy/hello/roy

Note that the URI used to address the service depends on the project/build/finalName element in the pom.xml file, as well as the @Path annotations on the endpoint in HelloEndpoints.java.  Mapping of these URIs to the servlet must also be configured in the web.xml file.

The project includes a sample ContainerRequestFilter (HTTPBasicAuthFilter.java) to demonstrate how you might wish to handle authentication/authorization.  The filter stores a copy of the userID/password in the request as a demonstration of how you can pass information along to your endpoint method.

Note that the pom.xml probably contains some unnecessary dependencies, since I've been testing some other things as I work on this...

