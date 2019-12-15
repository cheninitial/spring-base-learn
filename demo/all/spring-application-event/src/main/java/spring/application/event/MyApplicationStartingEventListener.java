package spring.application.event;

import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;

public class MyApplicationStartingEventListener implements ApplicationListener<ApplicationStartingEvent>, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(ApplicationStartingEvent event) {
        System.out.println("ApplicationListener<ApplicationStartingEvent>" + "applicationContext : " + applicationContext);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        applicationContext = applicationContext;
    }
}
