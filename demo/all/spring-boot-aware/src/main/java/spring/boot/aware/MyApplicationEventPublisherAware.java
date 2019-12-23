package spring.boot.aware;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

@Component
public class MyApplicationEventPublisherAware implements ApplicationEventPublisherAware {

    private final String NAME = "ApplicationEventPublisherAware";

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    private ApplicationEventPublisher applicationEventPublisher2;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        applicationEventPublisher2 = applicationEventPublisher;
    }

    @Override
    public String toString() {
        System.out.println(String.format("[%s] - Autowired.applicationEventPublisher: %s", NAME, applicationEventPublisher.hashCode()));
        System.out.println(String.format("[%s] - ApplicationEventPublisherAware.setApplicationEventPublisher(): %s", NAME, applicationEventPublisher2.hashCode()));
        System.out.println("=====================");
        return super.toString();
    }
}
