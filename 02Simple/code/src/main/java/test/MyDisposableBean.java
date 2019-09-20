package test;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
@Order(9)
public class MyDisposableBean implements DisposableBean {

    @Override
    public void destroy() throws Exception {

        System.out.println("MyDisposableBean");

    }
}
