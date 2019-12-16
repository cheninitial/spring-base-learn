package spring.application.exit;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
public class MyDisposableBean implements DisposableBean {

    @Override
    public void destroy() throws Exception {
        System.out.println("MyDisposableBean.destroy()");
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("MyDisposableBean.preDestroy()");
    }


}
