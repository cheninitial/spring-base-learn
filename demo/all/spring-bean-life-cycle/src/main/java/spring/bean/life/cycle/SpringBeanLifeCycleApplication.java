package spring.bean.life.cycle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringBeanLifeCycleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBeanLifeCycleApplication.class, args);
    }

    @Bean(initMethod = "initMethod", destroyMethod = "destroyMethod")
    public BeanByBean beanByBean() {
        return new BeanByBean();
    }

    @Autowired
    private ApplicationContext applicationContext;



}
