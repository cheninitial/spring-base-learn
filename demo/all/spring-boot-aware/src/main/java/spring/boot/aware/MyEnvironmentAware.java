package spring.boot.aware;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class MyEnvironmentAware implements EnvironmentAware {

    private final String NAME = "EnvironmentAware";

    @Autowired
    private Environment environment;

    private Environment environment2;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void setEnvironment(Environment environment) {
        environment2 = environment;
    }

    @Override
    public String toString() {
        System.out.println(String.format("[%s] - @Autowired.environment: %s", NAME, environment.hashCode()));
        System.out.println(String.format("[%s] - EnvironmentAware.setEnvironment(): %s", NAME, environment2.hashCode()));
        System.out.println(String.format("[%s] - applicationContext.getEnvironment(): %s", NAME, applicationContext.getEnvironment().hashCode()));
        System.out.println("=====================");
        return super.toString();
    }
}
