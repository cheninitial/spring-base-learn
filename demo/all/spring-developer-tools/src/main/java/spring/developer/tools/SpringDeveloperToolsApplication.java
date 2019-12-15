package spring.developer.tools;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SpringDeveloperToolsApplication {

    @GetMapping("/")
    String hello() {
        return "hello";
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringDeveloperToolsApplication.class);
    }

}
