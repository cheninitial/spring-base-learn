package spring.application.arguments;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Value("${server.port}")
    public String value;

    @GetMapping("/")
    String hello() {
        return value;
    }
}
