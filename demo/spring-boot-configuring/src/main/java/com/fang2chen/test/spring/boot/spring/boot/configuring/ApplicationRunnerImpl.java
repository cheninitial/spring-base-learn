package com.fang2chen.test.spring.boot.spring.boot.configuring;

import com.fang2chen.test.spring.boot.spring.boot.configuring.component.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties({MyProfilesBean.class, MyProfilesValidBean.class})
public class ApplicationRunnerImpl implements ApplicationRunner {

    @Autowired
    private MyRandomBean myRandomBean;

    @Autowired
    private MyRandomBean2 myRandomBean2;

    @Autowired
    private MyAppBean myAppBean;

    @Autowired
    private MyProfilesBean myProfilesBean;

    @Autowired
    private Environment environment;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("myRandomBean :  " + myRandomBean);
        System.out.println("myRandomBean2 :  " + myRandomBean2);
        System.out.println("myAppBean :  " + myAppBean);
        System.out.println("myProfilesBean :  " + myProfilesBean);
        System.out.println("environment.getProperty(\"my.random.bean.uuid\"):  " + environment.getProperty("my.random.bean.uuid"));
    }
}
