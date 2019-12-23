package spring.boot.aware;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.ServerEndpoint;

@Component
@RestController()
public class MyApplicationContextAware implements ApplicationContextAware {

    private final String NAME = "ApplicationContextAware";

    @Autowired
    private ApplicationContext applicationContex;

    private ApplicationContext applicationContext2;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext2 = applicationContext;
    }

    @Override
    @Bean
    public String toString() {
        System.out.println(String.format("[%s] - Autowired.applicationContex: %s", NAME, applicationContex.hashCode()));
        System.out.println(String.format("[%s] - ApplicationContextAware.setApplicationContext(): %s", NAME, applicationContext2.hashCode()));
        System.out.println("=====================");
        return super.toString();
    }
}
