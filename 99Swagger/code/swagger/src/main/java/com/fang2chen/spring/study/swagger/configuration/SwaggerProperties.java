package com.fang2chen.spring.study.swagger.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *     swagger 配置类
 * </p>
 * @author yuanliang.chen
 * @date 2019/9/25
 */
@ConfigurationProperties(prefix = "swagger.person")
@Data
public class SwaggerProperties {


    /**
     * 进行扫描的包
     * 如果不写的话就是扫描当前包及其子包
     * */
    private List<String> basePackage;

    /**
     * ui的题目
     * */
    private String title = "swagger-ui";

    /**
     * 版本
     * */
    private String version = "1.0.0";

    /**
     * 协议
     * */
    private List<String> protocols = Arrays.asList("http");

    /**
     * host也就是页面上发起请求的时候填写的地址
     * */
    private String host;

    /**
     * 联系人
     * */
    private String contactName;

    /**
     * 联系url
     * */
    private String contactUrl;

    /**
     * 联系用email
     * */
    private String contactEmail;

    /**
     * 描述信息
     * */
    private String description;

}
