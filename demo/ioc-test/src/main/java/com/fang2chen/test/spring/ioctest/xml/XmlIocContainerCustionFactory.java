package com.fang2chen.test.spring.ioctest.xml;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class XmlIocContainerCustionFactory {

    public static void main(String[] args) {

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("dao.xml");

        HelloServiceImpl helloService = context.getBean("helloService", HelloServiceImpl.class);
        String hello = helloService.hello();

    }

}
