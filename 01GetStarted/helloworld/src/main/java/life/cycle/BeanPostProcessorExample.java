package life.cycle;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

@Component
public class BeanPostProcessorExample implements BeanPostProcessor, Ordered {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("BeanPostProcessor 调用 postProcessBeforeInitialization 的方法， 参数 [ " + bean.getClass().getSimpleName() + "] [ " + beanName + "]");
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("BeanPostProcessor 调用 postProcessAfterInitialization 的方法， 参数 [ " + bean.getClass().getSimpleName() + "] [ " + beanName + "]");
        return bean;
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
