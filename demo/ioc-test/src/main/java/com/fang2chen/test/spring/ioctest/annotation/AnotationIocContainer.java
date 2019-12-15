package com.fang2chen.test.spring.ioctest.annotation;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AnotationIocContainer {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext("com.fang2chen.test.spring.ioctest.annotation");

        HelloServiceImpl helloServiceImpl = context.getBean("helloService", HelloServiceImpl.class);
        System.out.println(helloServiceImpl.hello());
        System.out.println(helloServiceImpl.getName());
    }
}
