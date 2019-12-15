package spring.application.arguments;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringApplicationArgumentsApplication implements ApplicationRunner {
    public static void main(String[] args) {
        args = new String[]{"--debug", "--server.port=8087"};
        SpringApplication.run(SpringApplicationArgumentsApplication.class, args);
    }

    @Autowired
    private ApplicationArguments applicationArguments;

    @Value("${server.port}")
    public static String value;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("SpringApplicationArgumentsApplication: " + applicationArguments.containsOption("debug"));
        System.out.println("SpringApplicationArgumentsApplication: " + applicationArguments.containsOption("server.port"));
        System.out.println("SpringApplicationArgumentsApplication: " + applicationArguments.getOptionValues("server.port"));
        System.out.println("SpringApplicationArgumentsApplication: " + value);
    }
}
