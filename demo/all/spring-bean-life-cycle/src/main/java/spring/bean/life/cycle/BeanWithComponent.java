package spring.bean.life.cycle;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class BeanWithComponent implements InitializingBean, DisposableBean {

    @Autowired
    private AaBean aaBean;

    private final static String BEAN_NAME = "BeanWithAnnotation";

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        System.out.println(String.format("%s : 调用 Bean 的 setAttribute,  aaBean: %s", BEAN_NAME, aaBean));
        this.name = name;
    }

    public BeanWithComponent() {
        System.out.println(String.format("%s : 调用 Bean 的 constructor,  aaBean: %s", BEAN_NAME, aaBean));
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println(String.format("%s : 调用 Bean 的 postConstruct(),  aaBean: %s", BEAN_NAME, aaBean));
    }

    public void initMethod() {
        System.out.println(String.format("%s : 调用 Bean 的 initMethod(),  aaBean: %s", BEAN_NAME, aaBean));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println(String.format("%s : 调用 Bean 的 afterPropertiesSet(),  aaBean: %s", BEAN_NAME, aaBean));
    }

    @PreDestroy
    public void preDestroy(){
        System.out.println(String.format("%s : 调用 Bean 的 preDestroy(),  aaBean: %s", BEAN_NAME, aaBean));
    }

    @Override
    public void destroy() throws Exception {
        System.out.println(String.format("%s : 调用 Bean 的 destroy(),  aaBean: %s", BEAN_NAME, aaBean));
    }

    public void destroyMethod(){
        System.out.println(String.format("%s : 调用 Bean 的 destroyMethod(),  aaBean: %s", BEAN_NAME, aaBean));
    }

}
