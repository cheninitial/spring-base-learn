package spring.integration.webservice;

import com.sun.xml.internal.ws.transport.http.server.EndpointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.ws.Endpoint;

@Configuration
public class WebServiceConfig {

    @Bean
    public Endpoint getEndpoint() {
        MyService myService = new MyServiceImpl();
        Endpoint publish = EndpointImpl.publish("http://localhost:8081/show", myService);
        return publish;
    }

}
