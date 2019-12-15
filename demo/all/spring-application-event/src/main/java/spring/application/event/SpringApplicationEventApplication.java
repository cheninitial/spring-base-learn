package spring.application.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringApplicationEventApplication {
    public static void main(String[] args) {
//        SpringApplication.run(SpringApplicationEventApplication.class, args);
        SpringApplication app = new SpringApplication(SpringApplicationEventApplication.class);
        app.addListeners(
                new MyApplicationStartingEventListener(),
                new MyApplicationEnvironmentPreparedEventListener(),
                new MyApplicationPreparedEventListener(),
                new MyApplicationStartedEventListener(),
                new MyApplicationReadyEventListener(),
                new MyApplicationFailedEventListener()
        );
        app.run(args);
    }
}
