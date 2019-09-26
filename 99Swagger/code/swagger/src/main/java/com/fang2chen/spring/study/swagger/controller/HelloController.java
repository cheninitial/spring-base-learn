package com.fang2chen.spring.study.swagger.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *     hello 控制器
 * </p>
 * @author yuanliang.chen
 * @date 2019/9/25
 */
@RestController
@Api
public class HelloController {

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    @ApiOperation("say hello")
    public String hello() {
        return "hello";
    }



}
