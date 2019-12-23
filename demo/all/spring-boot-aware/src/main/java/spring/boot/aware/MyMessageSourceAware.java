package spring.boot.aware;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Component;

@Component
public class MyMessageSourceAware implements MessageSourceAware {

    private final String NAME = "MessageSourceAware";

    @Autowired
    private MessageSource messageSource;

    private MessageSource messageSource2;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void setMessageSource(MessageSource messageSource) {
        messageSource2 = messageSource;
    }

    @Override
    public String toString() {
        System.out.println(String.format("[%s] - @Autowired.messageSource: %s", NAME, messageSource.hashCode()));
        System.out.println(String.format("[%s] - MessageSourceAware.setMessageSource(): %s", NAME, messageSource2.hashCode()));
        System.out.println(String.format("[%s] - applicationContext: %s", NAME, applicationContext.hashCode()));
        System.out.println("=====================");
        return super.toString();
    }
}
