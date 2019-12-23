package spring.boot.aware;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class MyResourceLoaderAware implements ResourceLoaderAware {

    private final String NAME = "ResourceLoaderAware";

    private ResourceLoader resourceLoader2;
    private final ResourceLoader resourceLoader;

    @Autowired
    public MyResourceLoaderAware(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader2 = resourceLoader;
    }

    @Override
    public String toString() {

        System.out.println(String.format("[%s] - ResourceLoaderAware.resourceLoader: %s", NAME, resourceLoader2));
        System.out.println(String.format("[%s] - @Autowired.resourceLoader: %s", NAME, resourceLoader));
        System.out.println("=====================");
        return super.toString();
    }
}
