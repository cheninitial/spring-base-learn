package com.fang2chen.test.spring.ioctest.groovy;

import com.fang2chen.test.spring.ioctest.xml.HelloServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericGroovyApplicationContext;

public class GroovyIocContainer {

    public static void main(String[] args) {
        ApplicationContext context = new GenericGroovyApplicationContext("service.groovy", "dao.groovy");

        HelloServiceImpl helloServiceImpl = context.getBean("helloService", HelloServiceImpl.class);
        System.out.println(helloServiceImpl.hello());
        System.out.println(helloServiceImpl.getName());



    }

}
