package life.cycle;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
public class BeanPostProcessorExample2 implements BeanPostProcessor, Ordered {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("BeanPostProcessor2 调用 postProcessBeforeInitialization 的方法， 参数 [ " + bean.getClass().getSimpleName() + "] [ " + beanName + "]");
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("BeanPostProcessor2 调用 postProcessAfterInitialization 的方法， 参数 [ " + bean.getClass().getSimpleName() + "] [ " + beanName + "]");
        return bean;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
