package spring.bean.life.cycle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ApplicationTestRunner implements ApplicationRunner {

    @Autowired
    private AnnotationConfigServletWebServerApplicationContext applicationContext;


    @Override
    public void run(ApplicationArguments args) throws Exception {

        System.out.println("ApplicationTestRunner.run");

        /**
         * 这种方法初始化的 Bean 没有生命周期
         * */
        applicationContext.register(BeanByContent.class);

        /**
         * 这种方法初始化的 Bean 没有生命周期
         * */
//        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(BeanByContent.class);
//        BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
//        BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry) applicationContext.getBeanFactory();
//        beanFactory.registerBeanDefinition("beanByContent", beanDefinition);
//
//
//        Object beanByContent = applicationContext.getBean("beanByContent");
//        System.out.println(beanByContent);


        return;
    }
}
