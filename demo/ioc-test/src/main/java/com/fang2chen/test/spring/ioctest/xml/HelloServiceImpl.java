package com.fang2chen.test.spring.ioctest.xml;

public class HelloServiceImpl {

    private String name;

    private HelloConfig helloConfig;

    public String hello() {
        return "hello   " + helloConfig.getExt();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HelloConfig getHelloConfig() {
        return helloConfig;
    }

    public void setHelloConfig(HelloConfig helloConfig) {
        this.helloConfig = helloConfig;
    }
}
