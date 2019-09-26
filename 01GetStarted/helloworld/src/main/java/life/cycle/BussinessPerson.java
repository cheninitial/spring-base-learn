package life.cycle;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class BussinessPerson implements BeanNameAware,
        BeanFactoryAware, ApplicationContextAware, InitializingBean, DisposableBean {

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        System.out.println("[" + this.getClass().getSimpleName() + "] 调用了 BeanFactoryAware 的setBeanFactory方法");
    }

    @Override
    public void setBeanName(String s) {
        System.out.println("[" + this.getClass().getSimpleName() + "] 调用了 BeanNameAware 的 setBeanName 方法");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("[" + this.getClass().getSimpleName() + "] 调用了 DisposableBean 的 destroy 方法");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("[" + this.getClass().getSimpleName() + "] 调用了 InitializingBean 的 afterPropertiesSet 方法");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("[" + this.getClass().getSimpleName() + "] 调用了 ApplicationContextAware 的 setApplicationContext 方法");
    }

    public BussinessPerson() {
        System.out.println("[" + this.getClass().getSimpleName() + "] 调用了构造函数");
    }

    @PostConstruct
    public void init() {
        System.out.println("[" + this.getClass().getSimpleName() + "] 调用了 @PostConstruct ");
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("[" + this.getClass().getSimpleName() + "] 调用了 @PreDestroy ");
    }


}
