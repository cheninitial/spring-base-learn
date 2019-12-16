package spring.configuration.considered.order;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringConfigurationConsideredOrderApplication implements ApplicationRunner {
    public static void main(String[] args) {
//        args = new String[]{"--name=\"command line\""};
        SpringApplication springApplication = new SpringApplication(SpringConfigurationConsideredOrderApplication.class);
        springApplication.run(args);
    }

    @Value("${name}")
    private String name;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("name 参数： " + name);
    }
}
