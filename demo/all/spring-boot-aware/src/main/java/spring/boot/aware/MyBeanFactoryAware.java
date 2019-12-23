package spring.boot.aware;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MyBeanFactoryAware implements BeanFactoryAware {

    private final String NAME = "BeanFactoryAware";

    @Autowired
    private BeanFactory beanFactory;

    private BeanFactory beanFactory2;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        beanFactory2 = beanFactory;
    }

    @Override
    public String toString() {
        System.out.println(String.format("[%s] - Autowired.beanFactory: %s", NAME, beanFactory.hashCode()));
        System.out.println(String.format("[%s] - BeanFactoryAware.beanFactory2: %s", NAME, beanFactory2.hashCode()));
        System.out.println("=====================");
        return super.toString();
    }
}
