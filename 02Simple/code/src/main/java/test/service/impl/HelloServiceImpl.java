package test.service.impl;

import org.springframework.stereotype.Service;
import test.service.HelloService;

//@Service
public class HelloServiceImpl  implements HelloService {

    public HelloServiceImpl() {
        System.out.println("1 HelloServiceImpl 构造函数");
    }

    public void postConstruct() {
        System.out.println("2 HelloServiceImpl postConstruct");
    }

    public void preDestory(){
        System.out.println(" HelloServiceImpl preDestory");
    }

    @Override
    public String hello() {
        return this.getClass() + ": hello";
    }


}
