package spring.admin.features;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.admin.SpringApplicationAdminMXBean;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAdminFeaturesApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAdminFeaturesApplication.class, args);
    }

}
