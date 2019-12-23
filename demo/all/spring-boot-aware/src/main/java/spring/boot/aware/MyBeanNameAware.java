package spring.boot.aware;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MyBeanNameAware implements BeanNameAware {

    private final String NAME = "BeanNameAware";

    private String beanName;

    @Override
    public void setBeanName(String name) {
        beanName = name;
    }

    @Override
    public String toString() {
        System.out.println(String.format("[%s] - BeanNameAware.setBeanName: %s", NAME, beanName));
        System.out.println("=====================");
        return super.toString();
    }
}
