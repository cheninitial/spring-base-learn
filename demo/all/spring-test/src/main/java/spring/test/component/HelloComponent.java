package spring.test.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HelloComponent {

    @Value("${hello}")
    private String name;

    public String getName() {
        return name;
    }

}
