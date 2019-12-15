package com.fang2chen.test.spring.boot.spring.boot.configuring.component;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class MyAppBean {

//    app.name=MyApp
//    app.description=${app.name} is a Spring Boot application
//    app.pom.description=@description@

    @Value("${app.name}")
    private String name;

    @Value("${app.description}")
    private String description;

    @Value("${app.pom.description}")
    private String pomDescription;

}
