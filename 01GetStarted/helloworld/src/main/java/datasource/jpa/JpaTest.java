package datasource.jpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "datasource.jpa")
@EnableJpaRepositories(basePackages = "datasource.jpa.dao")
@EntityScan(basePackages = "datasource.jpa.pojo")
public class JpaTest {

    public static void main(String[] args) {
        SpringApplication.run(JpaTest.class, args);
    }

}
