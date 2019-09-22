package test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.env.RandomValuePropertySource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import test.listeners.MyListener;
import test.properties.PersonProperties;
import test.service.HelloService;
import test.service.impl.HelloServiceImpl;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.Properties;

//@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@SpringBootApplication
@RestController
//@Import({MyCommandLineRunner.class, MyApplicationRunner.class, HelloServiceImpl.class})
@Import({MyCommandLineRunner.class, MyApplicationRunner.class})
@EnableConfigurationProperties(PersonProperties.class)
//@ImportResource(locations ="classpath:conf/application.yml")
//@ImportResource(locations = "classpath:beans.xml")
@PropertySource(value = {"classpath:conf/propertySource.yml", "classpath:conf/randomValuePropertySource.yml"})
public class Application {

    @Autowired
    private ApplicationContext applicationContext;

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

        Properties properties = new Properties();
        properties.setProperty("name", "Tom springApplication.setDefaultProperties");
        springApplication.setDefaultProperties(properties);



    }

    @PreDestroy
    @Order(10)
    public void exitPreDo() {
        System.out.println("PreDestroy.exitPreDo");
    }


//    @Bean(initMethod = "postConstruct", destroyMethod = "preDestory")
//    public HelloService helloService() {
//        return new HelloServiceImpl();
//    }


}
