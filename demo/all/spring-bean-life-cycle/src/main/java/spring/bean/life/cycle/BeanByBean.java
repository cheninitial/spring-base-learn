package spring.bean.life.cycle;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class BeanByBean implements InitializingBean, DisposableBean {

    private final static String BEAN_NAME = "BeanByBean";

    private String name;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        System.out.println(String.format("%s : 调用 Bean 的 setAttribute", BEAN_NAME));
        this.name = name;
    }

    public BeanByBean() {
        System.out.println(String.format("%s : 调用 Bean 的 constructor", BEAN_NAME));
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println(String.format("%s : 调用 Bean 的 postConstruct()", BEAN_NAME));
    }

    public void initMethod() {
        System.out.println(String.format("%s : 调用 Bean 的 initMethod()", BEAN_NAME));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println(String.format("%s : 调用 Bean 的 afterPropertiesSet()", BEAN_NAME));
    }

    @PreDestroy
    public void preDestroy(){
        System.out.println(String.format("%s : 调用 Bean 的 preDestroy()", BEAN_NAME));
    }

    @Override
    public void destroy() throws Exception {
        System.out.println(String.format("%s : 调用 Bean 的 destroy()", BEAN_NAME));
    }

    public void destroyMethod(){
        System.out.println(String.format("%s : 调用 Bean 的 destroyMethod()", BEAN_NAME));
    }

}
