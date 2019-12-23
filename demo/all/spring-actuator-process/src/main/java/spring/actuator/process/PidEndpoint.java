package spring.actuator.process;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.system.ApplicationPid;
import org.springframework.context.annotation.Configuration;

@Configuration
@Endpoint(id = "pid")
public class PidEndpoint {

    @ReadOperation
    public String pid() {
        return new ApplicationPid().toString();
    }

}
