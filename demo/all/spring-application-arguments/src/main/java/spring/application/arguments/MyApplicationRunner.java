package spring.application.arguments;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class MyApplicationRunner implements ApplicationRunner {

    @Value("${server.port}")
    public static String value;

    @Autowired
    private HelloController helloController;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("MyApplicationRunner:  " + value);
        System.out.println("MyApplicationRunner:  " + helloController);
    }
}
