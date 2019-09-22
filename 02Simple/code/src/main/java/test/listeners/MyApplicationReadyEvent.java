package test.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MyApplicationReadyEvent implements ApplicationListener<ApplicationReadyEvent> {

    @Value("${name: Tome@Value}")
    private String name;

    @Value("${names: name1,name2,name3,name4}")
    private List<String> names;


    @Autowired
    private Environment environment;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        System.out.println("ApplicationReadyEvent");
        System.out.println("property name is : " + name);
        System.out.println("property names is : " + names.size());
    }
}
