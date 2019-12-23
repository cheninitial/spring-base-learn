package spring.boot.aware;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootAwareApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootAwareApplication.class, args);
    }

    @Autowired
    MyBeanClassLoaderAware myBeanClassLoaderAware;

    @Autowired
    MyResourceLoaderAware myResourceLoaderAware;

    @Autowired
    MyBeanFactoryAware myBeanFactoryAware;

    @Autowired
    MyEnvironmentAware myEnvironmentAware;

    @Autowired
    private MyBeanNameAware myBeanNameAware;

    @Autowired
    private MyApplicationContextAware myApplicationContextAware;

    @Autowired
    private MyMessageSourceAware myMessageSourceAware;

    @Autowired
    private MyApplicationEventPublisherAware myApplicationEventPublisherAware;

    @Override
    public void run(String... args) throws Exception {
        myBeanClassLoaderAware.toString();
        myResourceLoaderAware.toString();
        myBeanFactoryAware.toString();
        myEnvironmentAware.toString();
        myBeanNameAware.toString();
        myApplicationContextAware.toString();
        myMessageSourceAware.toString();
        myApplicationEventPublisherAware.toString();
    }
}
