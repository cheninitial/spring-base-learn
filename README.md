# 系统要求

目标spring boot 版本是2.1.6-RELEASE



| 运行环境 | 版本 |
| -------- | ---- |
| java     | 1.8+ |



| 构建工具 | 版本 |
| -------- | ---- |
| Maven    | 3.3+ |
| Gradle   | 4.4+ |



| Servlet Container | Servelet Vserion |
| ----------------- | ---------------- |
| Tomcat 9.0        | 4.0              |
| Jetty 9.4         | 3.1              |
| Undertow 2.0      | 4.0              |



# 依赖管理

## 依赖版本管理

**依赖父pom**

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.1.6.RELEASE</version>
</parent>
```



**不使用父pom**

如果有自己的父pom的话，想要使用spring boot提供的版本管理，可以将```spring-boot-starter-parent```作为一个依赖放入到工程pom中。但是这样的话插件的版本就不能被管理到了。

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <!-- Import dependency management from Spring Boot -->
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>2.1.6.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```



**使用spring boot的maven 插件**

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

> Note
>
> 如果使用了start parent pom 的话只要加上依赖就可以了，不需要进行配置



## Starters

spring boot对于很多功能进行了封装，而且每个的名字形势都是```spring-boot-stater-*```



# 代码结构

```
com
    +- example
        +- myapplication
            +- Application.java
            |
            +- customer
            |	+- Customer.java
            |	+- CustomerController.java
            |	+- CustomerService.java
            |	+- CustomerRepository.java
            |
            +- order
                +- Order.java
                +- OrderController.java
                +- OrderService.java
                +- OrderRepository.java
```



```Application.java ``` 是主函数

```java
package com.example.myapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```



# 配置类

- XML sources 配置文件
- ```@Configuration``` 注解的类**（推荐）**



## 引入附加的配置类

- 在可以配扫描到的类上使用 ```@Import```  注解就可以引入其他配置类，这个类可以不加```@Configuration```注解
- 在可以扫描到的类上使用 ```@ComponentScan``` 注解可以指定扫描某个包中加了 ```@Configuration```注解的类



## 引入 XML 配置

使用 ```@ImportResource ``` 注解引入



# 自动配置

``@SpringBootApplication `` 或 ``@EnableAutoConfiguration ``注解

> Tip
>
> ``@SpringBootApplication `` 是一个组合注解，里面包含了``@EnableAutoConfiguration`` 注解
>
> ```java
> @Target({ElementType.TYPE})
> @Retention(RetentionPolicy.RUNTIME)
> @Documented
> @Inherited
> @SpringBootConfiguration
> @EnableAutoConfiguration
> @ComponentScan(
>     excludeFilters = {@Filter(
>     type = FilterType.CUSTOM,
>     classes = {TypeExcludeFilter.class}
> ), @Filter(
>     type = FilterType.CUSTOM,
>     classes = {AutoConfigurationExcludeFilter.class}
> )}
> )
> public @interface SpringBootApplication {
> 	...
> }
> ```



## 替换自动配置类

可以自己写一个与自动配置类同名的```Bean``` 就可以将其替换掉。

开一下 ``JDBC`` 数据的自动配置类

```java
@Configuration
@ConditionalOnClass({ DataSource.class, EmbeddedDatabaseType.class })
@EnableConfigurationProperties(DataSourceProperties.class)
@Import({ DataSourcePoolMetadataProvidersConfiguration.class,
		DataSourceInitializationConfiguration.class })
public class DataSourceAutoConfiguration {

	@Configuration
	@Conditional(EmbeddedDatabaseCondition.class)
	@ConditionalOnMissingBean({ DataSource.class, XADataSource.class })
	@Import(EmbeddedDataSourceConfiguration.class)
	protected static class EmbeddedDatabaseConfiguration {

	}

	@Configuration
	@Conditional(PooledDataSourceCondition.class)
	@ConditionalOnMissingBean({ DataSource.class, XADataSource.class })
	@Import({ DataSourceConfiguration.Hikari.class, DataSourceConfiguration.Tomcat.class,
			DataSourceConfiguration.Dbcp2.class, DataSourceConfiguration.Generic.class,
			DataSourceJmxConfiguration.class })
	protected static class PooledDataSourceConfiguration {

	}
```

```@ConditionalOnClass({ DataSource.class, EmbeddedDatabaseType.class })``` 只有在 ```DataSource.class```存在的时候在进行配置

``@ConditionalOnMissingBean({ DataSource.class, XADataSource.class })``只有在缺少 ``DataSource.class`` 对应的Bean的时候在进行配置

而且 spring boot 的自动配置是会在所写项目的Bean之后注入，所以可以再项目中创建一个```DataSource.class```的 ``Bean``来替换



## 使对应的自动配置失效

一般来说自动配置类只要对应的 ``Class`` 存在就会进行自动的配置，但是有的时候我们不想让他进行自动装配，就可以在 ``@SpringApplication`` 或 ``EnableAutoConfiguration`` 中将其去除。

```
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@RestController
@Import({MyCommandLineRunner.class, MyApplicationRunner.class})
@EnableConfigurationProperties(PersonProperties.class)
public class Application {
	...
}
```



或者也可以在配置文件中去除，需要对应的全类名，Idea 中会有提示的。

```yaml
spring:
  autoconfigure:
    exclude: org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
```



关于这个的源码在 ```AutoConfigurationImportSelector.java``` 中， 会从注解中提取 ```exclude``` 和 ```excludeName``` 再加上 配置文件中``spring.autoconfigure.exclude`` 的值，结合起来看需要排除哪一些。

```java
/**
	 * Return any exclusions that limit the candidate configurations.
	 * @param metadata the source metadata
	 * @param attributes the {@link #getAttributes(AnnotationMetadata) annotation
	 * attributes}
	 * @return exclusions or an empty set
	 */
protected Set<String> getExclusions(AnnotationMetadata metadata,
                                    AnnotationAttributes attributes) {
    Set<String> excluded = new LinkedHashSet<>();
    excluded.addAll(asList(attributes, "exclude"));
    excluded.addAll(Arrays.asList(attributes.getStringArray("excludeName")));
    excluded.addAll(getExcludeAutoConfigurationsProperty());
    return excluded;
}

private List<String> getExcludeAutoConfigurationsProperty() {
    if (getEnvironment() instanceof ConfigurableEnvironment) {
        Binder binder = Binder.get(getEnvironment());
        return binder.bind(PROPERTY_NAME_AUTOCONFIGURE_EXCLUDE, String[].class)
            .map(Arrays::asList).orElse(Collections.emptyList());
    }
    String[] excludes = getEnvironment()
        .getProperty(PROPERTY_NAME_AUTOCONFIGURE_EXCLUDE, String[].class);
    return (excludes != null) ? Arrays.asList(excludes) : Collections.emptyList();
}
```



肯定很奇怪了，那需要加载哪一些自动配置类是在什么规定的内。答案在``spring-boot-autoconfigure`` 包的 ``resource/META-INF/spring.factories`` 文件中



# Spring Beans 和 DI（依赖注入）

``Bean`` 生成的名称，可以看下面的这个博客：

https://www.cnblogs.com/kevin-yuan/p/5437140.html



## 生成Bean的方法

### ``@Component``

在使用了 ``@SpringApplication`` 注解后 会自动扫描该类包下面的所有子包。

然后在需要生成``Bean`` 的类上使用 ``@Component`` 、`@Service`、 `@Repository`、 `@Controller` 等注解，就可以了。

``Bean`` 的名字有如下三种情况

- 注解中规定了的 ``value`` 值
- 类名前两个字母都是大写，则就是类名
- 类名第一个字母大写，则将类名的第一个字母编程小写，作为名称

> 疑问
>
> 为什么不直接把类名作为 ``Bean`` 的名字，还要再转化一下呢？



## 





# 附录

## 找不到依赖

```shell
# 在maven配置文件中增加镜像
$ vim ~/.m2/settings
<mirror>
    <id>mirrorId</id>
    <mirrorOf>central</mirrorOf>
    <name>Human Readable Name for this Mirror.</name>
    <url>http://central.maven.org/maven2/</url>
</mirror>
```









