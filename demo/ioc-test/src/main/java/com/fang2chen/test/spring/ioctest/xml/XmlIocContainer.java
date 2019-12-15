package com.fang2chen.test.spring.ioctest.xml;

import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

public class XmlIocContainer {

    public static void main(String[] args) {

//        ApplicationContext context = new ClassPathXmlApplicationContext("services.xml", "dao.xml");
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("services.xml");
//        GenericApplicationContext context = new GenericApplicationContext();
//        new XmlBeanDefinitionReader(context).loadBeanDefinitions("services.xml");
//        context.refresh();

        HelloServiceImpl helloServiceImpl = context.getBean("helloService", HelloServiceImpl.class);
        System.out.println(helloServiceImpl.hello());
        System.out.println(helloServiceImpl.getName());

        HelloServiceImpl helloService2 = context.getBean("helloService2", HelloServiceImpl.class);
        System.out.println(helloService2.hello());
        System.out.println(helloService2.getName());

    }

}
