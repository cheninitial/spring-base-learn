package spring.actuator.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@Endpoint(id = "port")
public class PortEndpoint {

    @Autowired
    Environment environment;

    @ReadOperation
    public String port() {
        return environment.getProperty("local.server.port");
    }

}
