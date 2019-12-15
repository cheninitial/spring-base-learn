package com.fang2chen.test.spring.boot.spring.boot.configuring.component;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@ConfigurationProperties("app.system")
@Data
@Validated
public class MyProfilesValidBean {

//    @NotNull
    private String host;

//    @NotEmpty
    public String port;



}
