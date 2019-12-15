package spring.bean.create;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(BeanCreateByImport.class)
public class SpringBeanCreateApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(SpringBeanCreateApplication.class, args);
    }

    @Bean
    public BeanCreateByBeanMethod beanCreateByBeanMethod() {
        return new BeanCreateByBeanMethod();
    }

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Autowired
    private BeanCreateByAnnotation beanCreateByAnnotation;

    @Autowired
    private BeanCreateByImport beanCreateByImport;

    @Autowired
    private BeanCreateByBeanMethod beanCreateByBeanMethod;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("ApplicationRunner.run()");

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(BeanCreateByApplicationContentManual.class);
        BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
        BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry) applicationContext.getBeanFactory();
        beanFactory.registerBeanDefinition("beanCreateByApplicationContentManual", beanDefinition);

        System.out.println(applicationContext.getBean(BeanCreateByApplicationContentManual.class));
        System.out.println(beanCreateByAnnotation);
        System.out.println(beanCreateByImport);
        System.out.println(beanCreateByBeanMethod);

    }
}
