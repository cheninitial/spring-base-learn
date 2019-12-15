package com.fang2chen.test.spring.boot.spring.boot.configuring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.Validator;

@SpringBootApplication
public class SpringBootConfiguringApplication {

    public static void main(String[] args) {
        new SpringApplication().setAdditionalProfiles();
        ConfigurableApplicationContext run = SpringApplication.run(SpringBootConfiguringApplication.class, args);
    }

//    @Bean
//    public static Validator configurationPropertiesValidator() {
//        return new SamplePropertiesValidator();
//    }

}
