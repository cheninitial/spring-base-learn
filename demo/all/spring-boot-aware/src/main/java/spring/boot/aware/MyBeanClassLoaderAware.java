package spring.boot.aware;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class MyBeanClassLoaderAware implements BeanClassLoaderAware {

    private final String NAME = "BeanClassLoaderAware";

    private ClassLoader classLoader;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public String toString() {
        System.out.println(String.format("[%s] - BeanClassLoaderAware.classLoader: %s", NAME, classLoader));
        System.out.println(String.format("[%s] - applicationContext.getClassLoader(): %s", NAME, applicationContext.getClassLoader()));
        System.out.println("=====================");
        return super.toString();
    }
}
