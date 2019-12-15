package com.fang2chen.test.spring.boot.spring.boot.configuring.component;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class MyRandomBean2 {

//    my.random.bean.secret=${random.value}
//    my.random.bean.number=${random.int}
//    my.random.bean.bignumber=${random.long}
//    my.random.bean.uuid=${random.uuid}
//    my.random.bean.number.less.than.ten=${random.int(10)}
//    my.random.bean.number.in.range=${random.int[1024,65536]}

    @Value("${my.random.bean.secret}")
    private String secret;

    @Value("${my.random.bean.number}")
    private Integer number;

    @Value("${my.random.bean.bignumber}")
    private Long bigNumber;

    @Value("${my.random.bean.uuid}")
    private String uuid;

    @Value("${my.random.bean.number.less.than.ten}")
    private Integer numberLessThanTen;

    @Value("${my.random.bean.number.in.range}")
    private Integer numberInRange;


}
