package com.fang2chen.test.spring.ioctest.annotation;

import org.springframework.context.annotation.Configuration;

@Configuration
public class HelloConfig {

    private String ext = "大声说";

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }
}
