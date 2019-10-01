package aop.spring;


import aop.spring.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

//@SpringBootApplication(scanBasePackages = "aop.spring")

@EnableAutoConfiguration
@ComponentScan("aop.spring")
public class TestApplication {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(TestApplication.class);
        UserService userService = ctx.getBean(UserService.class);
//        userService.printUser("chen");
        userService.printUser2("chen");

        System.out.println("#################");
//        userService.printUser(null);

//        SpringApplication.run(TestApplication.class, args);
    }
}
