package spring.auto.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.web.servlet.ConditionalOnMissingFilterBean;

@SpringBootApplication
public class SpringAutoConfigurationApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringAutoConfigurationApplication.class, args);
    }
}
