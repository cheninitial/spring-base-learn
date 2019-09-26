package com.fang2chen.spring.study.swagger.configuration;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.HashSet;
import java.util.List;

@Configuration
@EnableConfigurationProperties(SwaggerProperties.class)
@EnableSwagger2
@Slf4j
public class SwaggerConfiguration {

    @Autowired
    private SwaggerProperties swaggerProperties;

    /**
     * 创建API应用
     * apiInfo() 增加API相关信息
     * 通过select()函数返回一个ApiSelectorBuilder实例,用来控制哪些接口暴露给Swagger来展现，
     * 指定扫描的包路径来定义指定要建立API的目录。
     * */
    @Bean
    public Docket createRestApi() {

        Docket docket = new Docket(DocumentationType.SWAGGER_2);
        docket.protocols(new HashSet<>(swaggerProperties.getProtocols()));
        docket.apiInfo(apiInfo());
        docket.enable(true);
        docket.host(swaggerProperties.getHost());
        ApiSelectorBuilder apiSelectorBuilder = docket.select();
        apiSelectorBuilder.apis(basePackage(swaggerProperties.getBasePackage()));
        apiSelectorBuilder.paths(PathSelectors.any());
        docket = apiSelectorBuilder.build();

        return docket;

    }

    /**
     * 创建该API的基本信息（这些基本信息会展现在文档页面中）
     * @return ApiInfo
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(swaggerProperties.getTitle())
                .description(swaggerProperties.getDescription())
                .contact(
                        new Contact(swaggerProperties.getContactName(),
                                swaggerProperties.getContactUrl(),
                                swaggerProperties.getContactEmail()))
                .version(swaggerProperties.getVersion())
                .build();
    }

    /**
     * <p>
     *     生成扫描规则
     * </p>
     * @param basePackageList : 需要进行扫描的包列表
     * @return : Predicate<RequestHandler>
     */
    public static Predicate<RequestHandler> basePackage(final List<String> basePackageList) {
        return predicate -> declaringClass(predicate).transform(handlerPackage(basePackageList)).or(true);
    }

    private static Function<Class<?>, Boolean> handlerPackage(final List<String> basePackageList)     {
        return input -> {
            // 循环判断匹配
            for (String strPackage : basePackageList) {
                boolean isMatch = input.getPackage().getName().startsWith(strPackage);
                if (isMatch) {
                    return true;
                }
            }
            return false;
        };
    }

    private static Optional<? extends Class<?>> declaringClass(RequestHandler input) {
        return Optional.fromNullable(input.declaringClass());
    }

}
