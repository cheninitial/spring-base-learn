package spring.integration.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.Endpoint;

@RestController
@SpringBootApplication
public class SpringIntegrationWebserviceApplication {

    @Autowired
    private Endpoint endpoint;

    @RequestMapping
    public String testEndpoint() {
        return endpoint.toString();
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringIntegrationWebserviceApplication.class, args);
    }

}
