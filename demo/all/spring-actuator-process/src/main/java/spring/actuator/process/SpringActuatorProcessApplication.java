package spring.actuator.process;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.web.context.WebServerPortFileWriter;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
public class SpringActuatorProcessApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(SpringActuatorProcessApplication.class);
        springApplication.addListeners(new ApplicationPidFileWriter(), new WebServerPortFileWriter());
        springApplication.run(args);
    }

}
