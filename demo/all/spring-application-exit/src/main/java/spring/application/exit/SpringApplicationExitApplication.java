package spring.application.exit;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PreDestroy;

@SpringBootApplication
public class SpringApplicationExitApplication implements DisposableBean {

    public static void main(String[] args) {
        SpringApplication.run(SpringApplicationExitApplication.class, args);
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("SpringApplicationExitApplication.destroy()");
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("SpringApplicationExitApplication.preDestroy()");
    }

}
