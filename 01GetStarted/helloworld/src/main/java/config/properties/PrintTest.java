package config.properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@Component
public class PrintTest {

    @Autowired
    private ConfigProperties configProperties;

    public void printConfigProperties() {
        System.out.println(configProperties.toString());
    }

}
