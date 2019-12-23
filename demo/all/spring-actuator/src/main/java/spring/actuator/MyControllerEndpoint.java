package spring.actuator;

import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;

@Configuration
@RestControllerEndpoint(id = "myControllerEndpoint")
public class MyControllerEndpoint {

    @GetMapping("/")
    public String getMapping() {
        return "MyControllerEndpoint";
    }

}
