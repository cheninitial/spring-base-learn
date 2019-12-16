package spring.customer.banner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringCustomerBannerApplication {

    public static void main(String[] args) {

        SpringApplication springApplication = new SpringApplication(SpringCustomerBannerApplication.class);
        springApplication.run(args);
    }

}
