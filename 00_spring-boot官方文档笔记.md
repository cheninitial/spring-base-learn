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

`Spring`的`Bean`也就是控制反转`Inversion of Control`, `IOC`)

`Bean` 是 `Spring` 的管理对象

`Bean` 生成的名称，可以看下面的这个博客：

https://www.cnblogs.com/kevin-yuan/p/5437140.html



## `IOC` 容器

`Spring IOC` 容器是一个管理`Bean`的容器，必须实现 `BeanFactory` 接口

```java
public interface BeanFactory{
    // 前缀
    String FACTORY_BEAN_PREFIX = "&";
    
    Object getBean(String name) thorows BeanException;
    
    <T> T getBean(String name, Class<T> requiredType) throws BeanException;
    
    <T> T getBean(Class<T> requiredType) throws BeanException;
    
    Object getBean(String name, Object... args) throws BeanException;
    
    <T> T getBean(Class<T> requiredType, Object... args) throws BeanException;
    
    // 判断是否包含bean
    boolean containsBean(String name);
    
    // Bean是否单例
    
    
    boolean isSigleton(String name) throws NoSuchBeanDefinitionException;
    
    // 是否类型匹配
    boolean isTypeMatch(String name, ResolveType typeToMatch) throws NoSuchBeanDefinitionException;
    
    // 获取Bean的类型
    Class<?> getType(String name) throws NoSuchBeanDefinitionException;
    
    // 获取Bean的别名
    String[] getAliases(String name);
}
```



`Spring` 在 `BeanFactory` 的基础上还设计了更加高级的 `ApplicationContext` 接口，在现实中 `Spring IOC` 的容器都是 `ApplicationContext` 接口的实现。

`AnnotationConfigApplicationContext` 是 `ApplicationContext` 基于注解的实现，

他的简单代码示例：

```java
ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
ctx.getBean(User.class);
```





## 生成`Bean`的方法

### ``@Component``

在使用了 ``@SpringApplication`` 注解后 会自动扫描该类包下面的所有子包。

然后在需要生成``Bean`` 的类上使用 ``@Component`` 、`@Service`、 `@Repository`、 `@Controller` 等注解，就可以了。可以看`@Service`、 `@Repository`、 `@Controller`的源码，其实他们里面都只有一个`@Compnent`注解

``Bean`` 的名字有如下三种情况

- 注解中规定了的 ``value`` 值
- 类名前两个字母都是大写，则就是类名
- 类名第一个字母大写，则将类名的第一个字母编程小写，作为名称

> 提示
>
> 为什么不直接把类名作为 ``Bean`` 的名字，还要再转化一下呢？
>
> 1、个人理解：因为 Spring Boot 在其他的注解中会使用 方法名 或者 变量名来寻找对应的Bean，所以这里规定bean的默认名字也使用这个规范



**注解位置：**

类

**变量：**

| 变量名 | 类型   | 说明       |
| ------ | ------ | ---------- |
| value  | String | Bean的名称 |



### `@Bean`

```java
@Bean
public HelloService helloService() {
  return new HelloServiceImpl();
}
```



如果有需要注入的依赖则直接传入就可以了， Spring boot会根据类型寻找依赖，如果找不到则会根据变量名称来寻找依赖。

```java
@Configuration
public class AppConfig {
    @Bean
    public TransferService transferService(AccountRepository accountRepository) {
        return new TransferServiceImpl(accountRepository);
    }
}
```



``Bean`` 的名字有如下三种情况

- 注解中规定了的 ``name`` 或 ``value`` 的值
- 方法名称



**注解位置：**

方法

**变量：**

| 变量名        | 类型     | 说明                                    |
| ------------- | -------- | --------------------------------------- |
| value         | String[] | Bean的名称                              |
| name          | String[] | Bean的名称                              |
| initMethod    | String   | 构造函数前调用的函数 = `@PostConstruct` |
| destroyMethod | String   | 销毁的时候调用的函数 = ``@PreDestroy``  |



### `@Import`

在类上不需要加上``@Compnent``注解，只要在对应的``Configuration``中将其``@Import``就可以，类中的所有注解都会生效。



**注解位置：**

类

**变量：**

| 变量名 | 类型       | 说明             |
| ------ | ---------- | ---------------- |
| value  | Class<?>[] | 需要注入的所有类 |



### `@ComponentScan`

批量的扫描包来生成`Bean`， 需要类上有相应的注解。

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Repeatable(CompentScans.class)
public @interface ComponentScan {
    
    @Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Repeatable(ComponentScans.class)
public @interface ComponentScan {

	// 扫描包
	@AliasFor("basePackages")
	String[] value() default {};

	// 扫描包
	@AliasFor("value")
	String[] basePackages() default {};

	// 定义扫描的类
	Class<?>[] basePackageClasses() default {};

	// Bean名称生成器
	Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;

	// 作用域解析器
	Class<? extends ScopeMetadataResolver> scopeResolver() default AnnotationScopeMetadataResolver.class;

	// 作用域代理模式
	ScopedProxyMode scopedProxy() default ScopedProxyMode.DEFAULT;

	// 资源匹配模式
	String resourcePattern() default ClassPathScanningCandidateComponentProvider.DEFAULT_RESOURCE_PATTERN;

	// 开启默认过滤器
	boolean useDefaultFilters() default true;

	// 满足过滤器的条件时扫描
	Filter[] includeFilters() default {};

	// 当不满足过滤器条件的时候扫描
	Filter[] excludeFilters() default {};

	// 是否延迟初始化
	boolean lazyInit() default false;
    
    @Retention(RetentionPolicy.RUNTIME)
	@Target({})
	@interface Filter {

		// 过滤器类型，按照注解类型裸着正则表达式
		FilterType type() default FilterType.ANNOTATION;

		// 定义过滤的类
		@AliasFor("classes")
		Class<?>[] value() default {};

		// 定义过滤的类
		@AliasFor("value")
		Class<?>[] classes() default {};

		// 定义过滤方式
		String[] pattern() default {};

	}
    
    
}
```





### xml配置文件方式

在 `resources` 文件夹中创建 `beans.xml` 文件

```xml
<!--?xml version="1.0" encoding="UTF-8"?-->
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="helloService" class="test.service.impl.HelloServiceImpl"></bean>

</beans>
```

在 `Application` 中引入配置文件： `@ImportResource(locations = "classpath:beans.xml")`

> 提示
>
> 现在Spring Boot已经不推荐使用xml的方式来配置了。



## 获取`Bean`的方法

### `@Autowired`

这个方法适用于 `Bean` 来获取其他 `Bean`，也就是只有被 `Spring Boot` 扫描并生成的 `Bean` 中这个注解才会生效。

`@Autowired` 作者给了它很多的逻辑，所以它 识别Bean的顺序非常的丰富

- 根据类型获取，这里的类型可以是接口，可以是父类
- 根据变量名称，匹配 `Bean` 名称



**注解位置：**

类、构造器、方法

**变量：**

| 变量名   | 类型    | 说明     |
| -------- | ------- | -------- |
| required | boolean | 是否必须 |



消除注解歧义的方法

- `@Primary`: 规定某一个实现拥有优先权
- `@Qualifier`: 指定需要注入bean的名字



### `ApplicationContext` 中获取

`ApplicationContext` 也就是 Spring Boot的上下文，从中可以获取到所有的Bean

可以直接`@Autowired`获取AplicationContext

```java
@Autowired
private ApplicationContext applicationContext;
```

如果想要在全局都可以获取到`ApplicationContext` 可以将其获取到放入到 `static` 的变量中



### 消除歧义性

当 `@Autowired` 根据类型查找，找到有多个符合条件的`Bean`的时候就会出错。

#### `@Primary`

定义哪一个类具有优先性



#### `@Qualifier`

指定加载的名称



## `Bean` 的生命周期

```java
@Component
public class BussinessPerson implements BeanNameAware,
        BeanFactoryAware, ApplicationContextAware, InitializingBean, DisposableBean {

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        System.out.println("[" + this.getClass().getSimpleName() + "] 调用了 BeanFactoryAware 的setBeanFactory方法");
    }

    @Override
    public void setBeanName(String s) {
        System.out.println("[" + this.getClass().getSimpleName() + "] 调用了 BeanNameAware 的 setBeanName 方法");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("[" + this.getClass().getSimpleName() + "] 调用了 DisposableBean 的 destroy 方法");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("[" + this.getClass().getSimpleName() + "] 调用了 InitializingBean 的 afterPropertiesSet 方法");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("[" + this.getClass().getSimpleName() + "] 调用了 ApplicationContextAware 的 setApplicationContext 方法");
    }

    public BussinessPerson() {
        System.out.println("[" + this.getClass().getSimpleName() + "] 调用了构造函数");
    }

    @PostConstruct
    public void init() {
        System.out.println("[" + this.getClass().getSimpleName() + "] 调用了 @PostConstruct ");
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("[" + this.getClass().getSimpleName() + "] 调用了 @PreDestroy ");
    }
}
```



```java
@Component
public class BeanPostProcessorExample implements BeanPostProcessor, Ordered {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("BeanPostProcessor 调用 postProcessBeforeInitialization 的方法， 参数 [ " + bean.getClass().getSimpleName() + "] [ " + beanName + "]");
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("BeanPostProcessor 调用 postProcessAfterInitialization 的方法， 参数 [ " + bean.getClass().getSimpleName() + "] [ " + beanName + "]");
        return bean;
    }
    
    @Override
    public int getOrder() {
        return 1;
    }
}
```



**日志输出：**

```
[BussinessPerson] 调用了构造函数
[BussinessPerson] 调用了 BeanNameAware 的 setBeanName 方法
[BussinessPerson] 调用了 BeanFactoryAware 的 setBeanFactory 方法
[BussinessPerson] 调用了 ApplicationContextAware 的 setApplicationContext 方法
BeanPostProcessor 调用 postProcessBeforeInitialization 的方法， 参数 [ BussinessPerson] [ bussinessPerson]
[BussinessPerson] 调用了 @PostConstruct 
[BussinessPerson] 调用了 InitializingBean 的 afterPropertiesSet 方法
BeanPostProcessor 调用 postProcessAfterInitialization 的方法， 参数 [ BussinessPerson] [ bussinessPerson]
[BussinessPerson] 调用了 @PreDestroy 
[BussinessPerson] 调用了 DisposableBean 的 destroy 方法
```



所以`Bean`的生命周期是：

| 序号 | 接口                                                | 是否全局 | 方式 |
| ---- | --------------------------------------------------- | -------- | ---- |
| 1    | BeanNameAware.setBeanName()                         | ×        | 继承 |
| 2    | BeanFactoryAware.setBeanFactory()                   | ×        | 继承 |
| 3    | ApplicationContextAware.setApplicationContext()     | ×        | 继承 |
| 4    | BeanPostProcessor.postProcessBeforeInitialization() | √        | Bean |
| 5    | @PostConstruct()                                    | ×        | 注解 |
| 6    | InitializingBean.afterPropertiesSet()               | ×        | 继承 |
| 7    | BeanPostProcessor.postProcessAfterInitialization()  | √        | Bean |
| 8    | 存续期                                              |          |      |
| 9    | @PreDestroy()                                       | ×        | 注解 |
| 10   | DisposableBean.destroy()                            | ×        | 继承 |



多个 `BeanPostProcessor` 可以让其继承 `Ordered` 接口来规定其执行的顺序





# 运行程序

## `jar -jar`

```shell
$ java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n \
			-jar target/myapplication-0.0.1-SNAPSHOT.jar
```

## 使用 maven plugin

```shell
$ mvn spring-boot:run
```

## 热部署

使用 `Jerebel`



# 开发者工具

还没有开具体有什么用



# `Application`

## `Application` 的时间和监听器

- `ApplicationStartingEvent`：启动前，什么都没有做的时候
- `ApplicationEncironmentPreparedEvent`：`Environment`完成j加载的时候
- `ApplicationPreparedEvent`：完成 Bean 的加载
- `ApplicationStartedEvent`：所有都准备完成
- `ApplicationReadyEvent`：完全准备完成
- `ApplicationFailedEvent`：启动失败的时候



```java
public class MyListener implements
        ApplicationListener<ApplicationStartingEvent> {

    @Override
    public void onApplicationEvent(ApplicationStartingEvent applicationStartingEvent) {
        System.out.println("\n spring boot 启动时间 \n");
    }
}
```



使其生效有两种方法：

调用`SpringApplication.addListeners()`方法

```java
springApplication.addListeners(new MyListener());
```

增加配置文件 `resource/META-INF/spring.factories`

```properties
org.springframework.context.ApplicationListener=test.listeners.MyListener
```



## 获取 `Arguments` 

从 `ApplicationArguments` Bean中获取

```java
@Component
public class MyBean {
@Autowired
public MyBean(ApplicationArguments args) {
  boolean debug = args.containsOption("debug");
    List<String> files = args.getNonOptionArgs();
    // if run with "--debug logfile.txt" debug=true, files=["logfile.txt"]
  }
}
```



## 使用 `ApplicationRunner` 或 `CommandLineRunner`

可以在 Spring boot 启动后进行一些操作

```java
@Component
public class MyBean implements CommandLineRunner {
  public void run(String... args) {
    // Do something...
  }
}
```

多个 `ApplicationRunner` 和 `CommandLineRunner` 可以使用 `@Order` 注解来规定其书序。

`@Order` 是数值越大优先级越高



## `Application Exit`

SpirngApplication 在JVM上注册了一个关闭的钩子，我们可以使用

- `DisposableBean`接口的Bean 
- `@PreDestroy`

来自定义程序退出的时候需要进行的操作。



# 外部配置

Spring boot 提供丰富的外化配置的功能，我们可以使用

- `@Vaule`
- `Environment` Bean
- `@ConfigurationProperties` 然后使用 `@Autowired`注入当成一个Bean来用

来获取到配置

## 配置的优先级

- `~/.spring-boot-devtolls.properties`， 开发工具的全局变量
- `@TestPropertySource` 测试用例规定的加载配置文件
- `@SpringBootTest#properties`
- 命令行参数
- 环境变量`SPRING_APPLICATION_JSON`
  - `$ SPRING_APPLICATION_JSON='{"acme":{"name":"test"}}' java -jar myapp.jar`
  - `$ java -Dspring.application.json='{"name":"test"}' -jar myapp.jar`
  - `$ java -jar myapp.jar --spring.application.json='{"name":"test"}'`
- `ServeletConfig`
- `ServeletContext`
- JNDI `java:comp/env`   没弄懂是什么
- Java 系统变量（`System.getProperties()`）`java -jar -Dname="Java System" myproject-0.0.1-SNAPSHOT.jar`
- 操作系统环境变量 `export name="OS"`
- `RandomValuePropertySource` 带有随机变量的配置   没成功试验出来
- `jar` 包外 `application-{profile}.properties`
- `jar` 包内 `application-{profile}.properties`
- `jar` 包外 `application.properties`
- `jar` 保内 `application.properties`
- `@PropertySource`
- 默认配置(`SpringApplication.setDefaultProperties`)  `@Value `中配置的默认值



## 配置的路径

- 当前目录下的 `/config`目录
- 当前目录
- `classpath` 的 `/config`
- `classpath` 目录



`spring.config.location` ：改变扫描的路径

`spring.config.name`： 改变扫描的文件名字



## `@Value`

可以在属性，方法，变量上注解，用于注入配置文件中的值，而且可以指定默认值

```java
@Value(${name})
private String name;

@Value(${name: Tom})
private String name;

@Value(${nams: name1,name2})
private List<String> names;
```



## `@ConfigurationProperties`

注解在类上，将一堆的配置信息转化为一个Bean。

使用的时候

```yaml
person:
  name: name1
  age: 12
```

```java
@ConfigurationProperties("person")
class Persion{
  private String name;
  private Integer age;
}
```

```java
@Configuration
@EnableConfigurationProperties(Persion.class)
	public class MyConfiguration {
}
```

```java
@Autowired
private Person person;
```



- 可以解析负载的 `Map` 对象

- List 对象需要写成

  ```yaml
  name:
  - name1
  - name2
  - name3
  ```

- 如果使用了 `Lombok` 那不能用为配置对象类添加任何的构造函数



## `@Value` 和 `@ConfigurationProperties` 的对比

|                        | `@ConfigurationProperties` | `@Value` |
| ---------------------- | -------------------------- | -------- |
| 松绑定                 | yes                        | no       |
| Configuration Metadata | yes                        | no       |
| SpEL                   | no                         | yes      |







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









