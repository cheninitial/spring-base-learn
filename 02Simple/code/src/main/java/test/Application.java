package test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@RestController
@Import({MyCommandLineRunner.class, MyApplicationRunner.class})
@EnableConfigurationProperties(PersonProperties.class)
public class Application {

    static {
        System.out.println("1.静态代码块");
    }

    {
        System.out.println("2.普通代码块");
    }

    public Application() {
        System.out.println("3.构造器");
    }

    @Value("${test.duration}")
    private Duration duration = Duration.ofSeconds(20);

    @PostConstruct
    public void postConstruct() {
        System.out.println("4.PostConstruct");
    }

    @Autowired
    private ApplicationArguments arguments;

    @Autowired
    private PersonProperties personProperties;

    @RequestMapping("/")
    public String hello() {

        System.out.println(arguments.containsOption("debug"));
        System.out.println(arguments.getNonOptionArgs());

        System.out.println("duration: " + duration.toString());

        return "hello  aaa";
    }

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(Application.class);
        springApplication.addListeners(new MyListener());
        springApplication.run(args);
    }

    @PreDestroy
    @Order(10)
    public void exitPreDo() {
        System.out.println("PreDestroy.exitPreDo");
    }


}
