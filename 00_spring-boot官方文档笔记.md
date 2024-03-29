[TOC]



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

在使用了 ``@SpringApplication`` 注解后 会自动扫描指定包下面的所有子包。

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



## 条件装配`Bean`

### `@Conditional`

`@Conditional` 注解可以自定义用于筛选的类方法，其需要实现 `Condition` 接口。

```java
public class DatabaseConditional implements Condition {
    
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetada metada){
        Environment env = context.getEnvironment;
        return env.containsProperty("database.driverName")
            && env.containsProperty("database.url");
    }
}
```



### `@ConditionalOnXXX`

- `@ConditionalOnBean` 当指定 `Bean`存在的时候装配
- `@ConditionalOnClass` 当指定类存在的时候装配
- `@ConditionalOnMissingBean` 当指定 `Bean` 不存在的时候装配
- `@ConditionalOnMissingClass` 当指定类不存在的时候装配
- `@ConditionalOnProperty` 当指定配置存在值得时候
  - `@ConditionalOnProperty(prefix = "spring.h2.console", name = "enabled", havingValue = "true", matchIfMissing = false)`



### `@Profile`

将根据 `spring.profiles.active` 和 `spring.profiles.default` 的值来决定时候加载。



## 动态注册 `Bean`

```java
@Component
@Slf4j
public class PersonBeanDefinitionRegistryPostProcessor
		implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
    throws BeansException {
        // 注册Bean定义，容器根据定义返回bean
        log.info("register personManager1>>>>>>>>>>>>>>>>");
        //构造bean定义
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
				.genericBeanDefinition(PersonManager.class);
        //设置依赖
        beanDefinitionBuilder.addPropertyReference("personDao", "personDao");
        BeanDefinition personManagerBeanDefinition = beanDefinitionBuilder
				.getRawBeanDefinition();
        //注册bean定义
        registry.registerBeanDefinition("personManager1", personManagerBeanDefinition);

	}

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
    throws BeansException {
        // 注册Bean实例，使用supply接口
        log.info("register personManager2>>>>>>>>>>>>>>>>");
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
            .genericBeanDefinition(PersonManager.class, () -> {
	         PersonDao personDao = beanFactory.getBean(PersonDao.class);
                 PersonManager personManager = new PersonManager();
                 personManager.setPersonDao(personDao);
                 return personManager;
         });
         BeanDefinition personManagerBeanDefinition = beanDefinitionBuilder
            .getRawBeanDefinition();
        ((DefaultListableBeanFactory) beanFactory)
          .registerBeanDefinition("personManager2", personManagerBeanDefinition);
    }
}
```



## `Bean` 的作用域

作用域也就是实例数是怎么分配的

| 作用域类型      | 说明                                                |
| --------------- | --------------------------------------------------- |
| `singleton`     | 默认值，`IoC`容器只存在单例                         |
| `prototype`     | 每当从`IoC`容器中取出一个`Bean`，则创建一个新的Bean |
| `session`       | HTTP会话                                            |
| `application`   | Web工程生命周期                                     |
| `request`       | Web工程单词请求                                     |
| `globalSession` | 在一个全局HTTP Session中                            |



定义作用域

-  `@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)`
-  `@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)`
- `@Scope(WebApplicationContext.SCOPE_REQUEST)`
- `@Scope(WebApplicationContext.SCOPE_SESSION)`
- `@Scope(WebApplicationContext.SCOPE_APPLICATION)`





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



# `Spring AOP`

面向切面编程，其中的基础就是动态代理模式，Spring Boot 使用量两种动态代理方式：

- `JDK` 动态代理：当代理对象有接口实现的时候；
  - 原理是在内存中生成一个代理对象并实现代理对象的所有接口
- `CGLIB` 动态代理：当代理对象没有接口实现的时候；
  - 原理shi在内存中生成一个代理对象并继承代理对象
  - 所有需要注意一些修饰为 `final` 的东西，因为这些东西是不能被继承的



## 动态代理简介

### `JDK` 动态代理

```java
public interface HelloService {
  String hello();
}
```

```java
public class HelloServiceImpl {
  @Override
  public String hello() {
    System.out.println("[HelloServiceImpl] 调用 hello() 方法");
    return "hello";
  }
}
```

```java
// 代理的处理方法
public class MyInvocationHandler implements InvocationHandler {
  
  private Object target;

  public MyInvocationHandler(Object target) {
    this.target = target;
  }
  
  @Override
  public Object invoke(Object proxy, Methode method, Object[] args) throws Throwable {
    System.out.println("调用前");
    Object object = method.invoke(target, args);
    System.out.println("调用后");
    return object;
  }
}
```

```java
public class Test {
  public static void main(String[] args){
    HelloService helloService = new HelloServiceImpl();
    HelloService proxy =
      (HelloService) Proxy.newProxyInstance(
      helloService.getClass().getClassLoader(),
      helloService.getClass().getInterfaces(),
      new MyInvocationHandler(helloService));
    proxy.hello();
  }
}
```



### CGLIB 动态代理

```java
public class HelloServiceImpl2 {
    public String hello() {
        System.out.println("[HelloServiceImpl] 调用 hello() 方法");
        return "hello";
    }
}
```

```java
public class CglibMethodInterceptor implements MethodInterceptor {
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.out.println("调用前");
        Object object = proxy.invokeSuper(obj, args);
        System.out.println("调用后");
        return object;
    }
}
```

```java
public class Test {
    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HelloServiceImpl2.class);
        enhancer.setCallback(new CglibMethodInterceptor());

        HelloServiceImpl2 proxy = (HelloServiceImpl2) enhancer.create();
        proxy.hello();
    }
}
```



## `AOP` 的实现(还没有开明白)

知道动态代理之后，我们理解 AOP 就比较容易了。因为我们执行的目标方法是`HelloService.hello()`，但是我们想要在执行方法前、后、出现异常等做一些特殊的事情，实现方法就是丰富 `InvocationHandler` 或 `MethodInterceptor` 的实现。



## `AOP` 的使用

使用 `@AspectJ` 注解方式来进行切面开发。 Spring AOP 只能对方法进行拦截，所以首选需要确认的是需要拦截什么方法。



pom 中引入依赖

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```



### 术语

- 连接点（jion point）：具体被拦截的对象
- 切点（point cut）：代表了一组连接点
- 通知（advice）：对应流程不同环节中织入的方法
  - 前置通知
  - 后置通知
  - 环绕通知
  - 事后返回通知
  - 异常通知
- 目标对象（target）：也就是被代理对象
- 引入（introduction）：引入新类和方法，增强现有Bean的功能
- 织入（weaving）：就是找到连接点，截获方法，增强Bean功能的这个流程
- 切面（aspect）：就是我们织入的那个约定



### `spring aop` 约定的流程

![image-20190928162702474](00_spring-boot官方文档笔记.assets/image-20190928162702474.png)



### 定义切面

**无参数切面**

```java
@Component
@Aspect
public class MyAspect {

    static {
        System.out.println("【MyAspect】 加载");
    }
		
    // 定义切点
    @Pointcut("execution(* aop.spring.service.UserServiceImpl.printUser(..))")
    public void pointCut() {
        System.out.println("[MyAspect] 调用 pointCut() 方法");
    }

    // 前置通知
    @Before("pointCut()")
    public void before() {
        System.out.println("[MyAspect] 调用 before() 方法");
    }

    // 后置通知
    @After("pointCut()")
    public void After() {
        System.out.println("[MyAspect] 调用 After() 方法");
    }

    // 返回通知
    @AfterReturning("pointCut()")
    public void AfterReturning() {
        System.out.println("[MyAspect] 调用 AfterReturning() 方法");
    }

    // 异常通知
    @AfterThrowing("pointCut()")
    public void AfterThrowing() {
        System.out.println("[MyAspect] 调用 AfterThrowing() 方法");
    }
}
```



**带上参数的切面**

```java
@Component
@Aspect
public class MyAspect2 {

    static {
        System.out.println("【MyAspect2】 加载");
    }

    @Pointcut("@annotation(aop.spring.aspect.AspectAnnotation)")
    public void pointCut() {
        System.out.println("[MyAspect2] 调用 pointCut() 方法");
    }

    @Before("pointCut() && args(name)")
    public void before(
            JoinPoint joinPoint, String name
    ) {
        System.out.println("[MyAspect2] 调用 before() 方法, name参数为： " + name);
    }

    @After("pointCut()")
    public void After() {
        System.out.println("[MyAspect2] 调用 After() 方法");
    }

    @AfterReturning("pointCut()")
    public void AfterReturning() {
        System.out.println("[MyAspect2] 调用 AfterReturning() 方法");
    }

    @AfterThrowing("pointCut()")
    public void AfterThrowing() {
        System.out.println("[MyAspect2] 调用 AfterThrowing() 方法");
    }
  	
    @Around("pointCut()")
    public void arroud(ProceedingJoinPoint joinPoint) throws Throwable{
        System.out.println("[MyAspect2] 调用 arroud() 方法， 前");
        joinPoint.proceed();
        System.out.println("[MyAspect2] 调用 arroud() 方法， 后");
    }
}
```



**环绕通知**

环绕同志是最强大的通知，但是也是最复杂的，它完全可以完成其他任何一个通知的功能。

在有些版本中 `@Before` 可能在 `@Aroud`之后执行的



### 切点表达式

- `execution()`： 正则表达式配置方法
  - `execution(* aop.spring.service.UserServiceImpl.printUser(..))`
    - `*`： 任意返回类型
    - `aop.spring.service.UserServiceImpl`：全路径类名
    - `printUser`：方法名
    - `(..)`：任意参数
- `args()`：方法参数
- `@args()`：和 arg() 作用相同
- `@annotation`：带指定注解的连接点



### 多个切面的执行顺序

使用 `@Order` 注解 或者 实现 `Ordered` 接口

至于方法执行的顺序，看一个答应就好了

```
MyAspect1 before .....
MyAspect2 before .....
MyAspect3 before .....
测试多切面的顺序
MyAspect3 after .....
MyAspect3 afterReturning .....
MyAspect2 after .....
MyAspect2 afterReturning .....
MyAspect1 after .....
MyAspect1 afterReturning .....
```



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

- 需要使用`@Component` 注解后 `@EnableConfigurationProperties` 注解，将其装配成 `Bean`



## `@Value` 和 `@ConfigurationProperties` 的对比

|                        | `@ConfigurationProperties` | `@Value` |
| ---------------------- | -------------------------- | -------- |
| 松绑定                 | yes                        | no       |
| Configuration Metadata | yes                        | no       |
| SpEL                   | no                         | yes      |



# 数据库编程

## 数据库配置

具体的配置可以随便百度一下就好了， 这里主要给出 `MySql` 的配置内容。



**pom**

```xml
<dependency>
  <groupId>mysql</groupId>
  <artifactId>mysql-connector-java</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
```



**配置文件中增加配置**

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3316/spring?useUnicode=true&characterEncoding=UTF-8
    username: root
    password: cyl199203
    tomcat:
      max-idle: 10
      max-active: 50
      max-wait: 10000
      initial-size: 5
    driver-class-name: com.mysql.cj.jdbc.Driver
```

需要注意的是 url 中携带的参数。

- `useUnicode`： 是否使用 Unicode
- `characterEncoding` ：使用字符编码， 对应不上的话中文可能会出现乱码
- `serverTimezone=CTT` ：设置的时区，CCT 是中国



## 操作数据库

### JdbcTemplate

这种c啊哦做已经不太使用了，这里大家了解一下就好了。

```java
@Service
public class JdbcTmplUserServiceImpl implements JdbcTmplUserService {

    @Autowired
    private JdbcTemplate jdbcTemplate = null;

    /**
     * <p>
     *     定义一个映射关系
     *     这个横向MyBatis中的 Mapper 的功能
     * </p>
     * @return : RowMapper<User>
     */
    private RowMapper<User> getUserMapper() {
        RowMapper<User> userRowMapper = new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet resultSet, int i) throws SQLException {
                User user = new User();
                user.setId(resultSet.getLong("id"));
                user.setUserName(resultSet.getString("user_name"));
                int sexId = resultSet.getInt("sex");
                SexEnum sexEnum = SexEnum.getEnumById(sexId);
                user.setSex(sexEnum);
                user.setNote(resultSet.getString("note"));
                return user;
            }
        };
        return userRowMapper;
    }

    @Override
    public User getUser(Long id) {
        String sql = "select id, user_name, sex, note from t_user where id = ?";
        Object[] params = new Object[]{id.toString()};
        // 直接写 id 会出现错误
//        Object[] params = new Object[]{id};
        User user = jdbcTemplate.queryForObject(sql, params, getUserMapper());
        return user;
    }

    @Override
    public List<User> findUsers(String userName, String note) {
        String sql = "select id, user_name, sex, note from t_user " +
                "where user_name like concat('%', ?, '%') " +
                "and note like concat('%', ? '%') ";
        Object[] params = new Object[]{userName, note};
        List<User> userList = jdbcTemplate.query(sql, params, getUserMapper());
        return userList;
    }

    @Override
    public int insertUser(User user) {
        String sql = "insert into t_user (user_name, sex, note) values (?,?,?)";
        return jdbcTemplate.update(sql, user.getUserName(), user.getSex().getId(), user.getNote());
    }

    @Override
    public int updateUser(User user) {
        String sql = "update t_user set user_name = ?, sex = ?, note = ? where id =?";
        return jdbcTemplate.update(sql, user.getUserName(), user.getSex().getId(), user.getNote(), user.getId());
    }

    @Override
    public int deleteUser(Long id) {
        String sql = "delete from t_user where id = ?";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public User getUser2(Long id) {
        User result = jdbcTemplate.execute(new StatementCallback<User>() {
            @Override
            public User doInStatement(Statement statement) throws SQLException, DataAccessException {
                String sql1 = "select count(*) as total from t_user where id = " + id;
                ResultSet rs1 = statement.executeQuery(sql1);
                while (rs1.next()) {
                    int total = rs1.getInt("total");
                    System.out.println(total);
                }

                String sql2 = "select id, user_name, sex, note from t_user where id = " + id;
                ResultSet rs2 = statement.executeQuery(sql2);
                User user = null;
                while (rs2.next()) {
                    int rowNum = rs2.getRow();
                    user = getUserMapper().mapRow(rs2, rowNum);
                }
                return user;
            }
        });

        return result;
    }

    @Override
    public User getUser3(Long id) {
        return jdbcTemplate.execute(new ConnectionCallback<User>() {
            @Override
            public User doInConnection(Connection connection) throws SQLException, DataAccessException {
                String sql1 = "select count(*) as total from t_user " +
                        "where id = ？";
                PreparedStatement ps1 = connection.prepareStatement(sql1);
                ps1.setString(1, id.toString());
                ResultSet rs1 = ps1.executeQuery();
                while (rs1.next()) {
                    System.out.println(rs1.getInt("total"));
                }

                String sql2 = "select id, user_name, sex, note from t_user " +
                        "where id = ?";
                PreparedStatement ps2 = connection.prepareStatement(sql2);
                ps2.setString(1, id.toString());
                ResultSet rs2 = ps2.executeQuery();
                User user = null;
                while (rs2.next()) {
                    int rowNum = rs2.getRow();
                    user = getUserMapper().mapRow(rs2, rowNum);
                }

                return user;
            }
        });
    }
}
```



### JPA(Hibernate)

JPA(java persistence API)， 是定义了对象关系映射（ORM）以及实体对象持久化的标准接口。JPA是依靠 Hibernate 得以实现的。

pom 中增加配置

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```



Spring boot 启动类中增加注解

```java
@SpringBootApplication(scanBasePackages = "datasource.jpa")
@EnableJpaRepositories(basePackages = "datasource.jpa.dao")
@EntityScan(basePackages = "datasource.jpa.pojo")
public class JpaTest {

    public static void main(String[] args) {
        SpringApplication.run(JpaTest.class, args);
    }

}
```

- EnableJpaRepositories： 规定定义了Repository 接口的类的位置
- EntityScan：对应实体的位置



实体类

```java
@Entity(name = "user")  // Entity 名称
@Table(name = "t_user")  // 对应表名
public class User {

    @Id		// 标记注解
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 标记主键值生成方式
    private Long id;

    @Column(name = "user_name")    // 属性名 和 表字段名 对应的关系
    private String userName;

    private String note;

    @Convert(converter = SexConverter.class)  // 特殊对象的转换关系
    private SexEnum sex;
  
  // getter / setter
}
```



转换工具对象

```java
public class SexConverter implements AttributeConverter<SexEnum, Integer> {

    @Override
    public Integer convertToDatabaseColumn(SexEnum sexEnum) {
        return sexEnum.getId();
    }

    @Override
    public SexEnum convertToEntityAttribute(Integer integer) {
        return SexEnum.getEnumById(integer);
    }
}
```



使用的方法

```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JpaTest.class)
public class JpaUserRepositoryTest {
    @Autowired
    private JpaUserRepository jpaUserRepository;
   
    @Test
    public void getUser() {
        User user = jpaUserRepository.findById(7L).get();
        System.out.println(user);
        
    }
}
```



### Mybatis

特点

- 基于 SQL 到 POJO 的模型，使用 XML 或者注解配置
- 自动映射， 驼峰映射等
- 动态SQL



可配置内容：

- properties：spring 进行配置
- settings：改变Mybatis底层行为
  - 映射规则
  - 执行器类型
  - 缓存等
- typeAliasses：类型别名
- typeHandlers： 类型处理器。javaType 和 JdbcType 的相互转化
- objectFactory：对象工厂。
- plugins：插件，拦截器。 通过动态代理和责任链模式
- environments：数据库连接内容和事务
- databaseIdProvider：数据库厂商标识
- mappers：映射器。SQL 和 POJO 的映射关系。



pom

```xml
<dependency>
  <groupId>org.mybatis.spring.boot</groupId>
  <artifactId>mybatis-spring-boot-starter</artifactId>
  <version>1.3.1</version>
</dependency>
```



pojo

```java
import datasource.enumeration.SexEnum;
import lombok.Data;
import org.apache.ibatis.type.Alias;

@Alias(value = "user")
@Data
public class User {

    private Long id;

    private String userName;

    private String note;

    private SexEnum sex = null;

}
```



type handler

```java
import datasource.enumeration.SexEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedJdbcTypes(JdbcType.INTEGER)
@MappedTypes(value = SexEnum.class)
public class SexTypeHandler extends BaseTypeHandler<SexEnum> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, SexEnum sexEnum, JdbcType jdbcType) throws SQLException {
        preparedStatement.setInt(i, sexEnum.getId());
    }

    @Override
    public SexEnum getNullableResult(ResultSet resultSet, String s) throws SQLException {
        int sex = resultSet.getInt(s);
        if (sex != 1 && sex != 2) {
            return null;
        }
        return SexEnum.getEnumById(sex);
    }

    @Override
    public SexEnum getNullableResult(ResultSet resultSet, int i) throws SQLException {
        int sex = resultSet.getInt(i);
        if (sex != 1 && sex != 2) {
            return null;
        }
        return SexEnum.getEnumById(sex);
    }

    @Override
    public SexEnum getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        int sex = callableStatement.getInt(i);
        if (sex != 1 && sex != 2) {
            return null;
        }
        return SexEnum.getEnumById(sex);
    }
}
```



dao

```java
import datasource.mybatis.pojo.User;
import org.springframework.stereotype.Repository;

@Repository
public interface MybatisUserDao {

    public User getUser(Long id);

}
```



xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="datasource.mybatis.dao.MybatisUserDao">
    <select id="getUser" parameterType="long" resultType="user">
        select id, user_name as userName, sex, note from t_user where id = #{id}
    </select>
</mapper>
```



配置文件

```yaml
mybatis:
  mapper-locations: classpath*:/mapper/*.xml
  type-aliases-package: datasource.mybatis.pojo
  type-handlers-package: datasource.mybatis.type.handler
```



启动文件注释

```java
@SpringBootApplication
@MapperScan("datasource.mybatis.dao")
public class MybatisTest {

    public static void main(String[] args) {
        SpringApplication.run(MybatisTest.class, args);
    }

}
```



# 数据库事务处理

数据库事务，简称事务，是数据库管理系统执行过程中的一个单元，由一个有限的数据库操作序列构成。



- spring 使用声明式事务编程
- 使用 `@Transactional` 注解



## `@Transactional`

可以注释在：

- 接口
- 实现类（推荐）

上就可以开始数据库事务



| 序号 | 名字                   | 类型                         | 说明                           |
| ---- | ---------------------- | ---------------------------- | ------------------------------ |
| 1    | value                  | String                       | 通过 bean 的名称指定事务处理器 |
| 2    | transactionManager     | String                       | 同 value                       |
| 3    | propagation            | Propagation                  | 指定事务传播的行为             |
| 4    | isolation              | Isolation                    | 指定隔离级别                   |
| 5    | timeout                | int                          | 指定超时时间， 单位 秒         |
| 6    | readOnly               | boolean                      | 是否为只读事务                 |
| 7    | rollbackFor            | Class<? extends Throwable>[] | 指定发生什么异常的时候回滚     |
| 8    | rollbackForClassName   | String[]                     | 指定回滚时的异常的全路径类名   |
| 9    | noRollbackFor          | Class<? extends Throwable>[] | 指定不会滚的异常               |
| 10   | noRollbackForClassName | String[]                     | 指定不会滚时的异常的全路径类名 |



### `isolation`隔离级别

#### 数据库事务知识

##### 数据库事务具有4个基本特征（ACID）

- Atomic（原子性）：要么都成功，要么都失败
- Consistency（一致性）：事务在完成时，必须所有的数据都保持一致状态
- Isolation（隔离性）：多线程访问的限制
- Durability（持久性）：事物结束，所有的数据固化



##### 丢失更新的各种情况

###### 第一类丢失

一个事务回滚了另一个事务的提交，大部分数据库都克服了这类丢失

| 时刻 | 事务1         | 事务2         |
| ---- | ------------- | ------------- |
| T1   | 初始库存，100 | 初始库存，100 |
| T2   | 扣减库存，99  | ——            |
| T3   | ——            | 扣减库存，99  |
| T4   | ——            | 提交事务，99  |
| T5   | 回滚事务，100 |               |



###### 第二类丢失

两个事务先后提交

| 时刻 | 事务1         | 事务2          |
| ---- | ------------- | -------------- |
| T1   | 初始库存，100 | 初始库存，1000 |
| T2   | 扣减库存，99  | ——             |
| T3   | ——            | 扣减库存，99   |
| T4   | ——            | 提交事务，99   |
| T5   | 提交事务，99  |                |



#### 详解隔离级别

数据库b标准有4类隔离级别：

- 未提交读
- 读写提交
- 可重复度
- 串行化



##### 未提交读

允许一个事务读取另一个事务未提交的数据

| 时刻 | 事务1       | 事务2    | 备注                                                         |
| ---- | ----------- | -------- | ------------------------------------------------------------ |
| T0   | ——          | ——       | 商品初始化为2                                                |
| T1   | 读取库存为2 |          |                                                              |
| T2   | 扣减库存    |          | 库存为1                                                      |
| T3   |             | 扣减库存 | 库存为0， 读取事务1未提交的库存数量                          |
| T4   |             | 提交事务 | 库存保存为0                                                  |
| T5   | 回滚事务    |          | 因为第一类丢失更新已经克服，所以不会回滚到库存2， 库存为0，结果错误 |

允许读取到未提交事务的值。



##### 读写提交

一个事务只能读取另一个事务已经提交的数据

| 时刻 | 事务1     | 事务2    | 备注                                   |
| ---- | --------- | -------- | -------------------------------------- |
| T0   | ——        | ——       | 库存初始2                              |
| T1   | 读取库存2 |          |                                        |
| T2   | 扣减库存  |          | 库存为1                                |
| T3   |           | 扣减库存 | 库存为1，读取不倒事务1未提交的库存事务 |
| T4   |           | 提交事务 | 库存为1                                |
| T5   | 回滚事务  |          | 库存结果为1                            |



但还是会出现不可重读场景

| 时刻 | 事务1     | 事务2       | 备注                            |
| ---- | --------- | ----------- | ------------------------------- |
| T0   |           |             | 初始化库存为1                   |
| T1   | 读取库存1 |             |                                 |
| T2   | 扣减库存  |             | 事务未提交                      |
| T3   |           | 读取库存为1 | 认为可以扣减                    |
| T4   | 提交事务  |             |                                 |
| T5   |           | 扣减库存    | 失败，因为此时库存为0，无法扣减 |



##### 可重复读

一个事务读取了数据之后，另外一个事务会被阻塞知道第一个事务被提交或者回滚

| 时刻 | 事务1     | 事务2        | 备注                      |
| ---- | --------- | ------------ | ------------------------- |
| T0   |           |              | 库存初始化为1             |
| T1   | 读取库存1 |              |                           |
| T2   | 扣减库存  |              | 事务未提交                |
| T3   |           | 尝试读取库存 | 不允许读取，等待事务1提交 |
| T4   | 提交事务  |              | 储存变为0                 |
| T5   |           | 读取库存     | 库存为0，无法扣减         |



但还是会出现幻读的情况

| 时刻 | 事务1           | 事务2              | 备注                                             |
| ---- | --------------- | ------------------ | ------------------------------------------------ |
| T0   | 读取库存50      |                    | 库存初始化为100， 现在已经销售500个，库存50      |
| T1   |                 | 查询交易记录，50笔 |                                                  |
| T2   | 扣减库存        |                    |                                                  |
| T3   | 插入1笔交易记录 |                    |                                                  |
| T4   | 提交事务        |                    | 库存49，交易记录51                               |
| T5   |                 | 打印交易记录，51笔 | 这里与查询的不一致，在事务2看来有1笔交易是虚幻的 |



##### 串行化

数据库最高隔离级别，要求所有SQL都按照顺序执行



##### 使用合理的隔离级别

|          | 脏读 | 不可重复读 | 幻读 |
| -------- | ---- | ---------- | ---- |
| 未提交读 | √    | √          | √    |
| 读写提交 | √    | √          | ×    |
| 可重复读 | √    | ×          | ×    |
| 串行化   | ×    | ×          | ×    |



-  脏读：事务读取到了另一个事务的值
- 不可重复度：事务未能读取到另外事务的值
- 幻读：存在于统计表的情况，统计表的变化慢于主表



### `isolation` 的使用

- `Isolation.DEFAULT(-1)`：数据库默认
- `Isolation.READ_UNCOMMITTED(1)`：未提交读
- `Isolation.READ_COMMITTED(2)`：读写提交
- `Isolation.REPEATABLE_READ(4)`：可重复读
- `Isolation.SERIALIZABLE(8)`： 串行化



配置文件：

```pro
# -1: 数据库默认隔离级别
# 1 ： 未提交读
# 2 ：读写提交
# 4 ：可重复读
# 8 ：串行化
# tomcat 数据源
spring.datasourc.tomcat.default-transaction-isolation=2
# dpcp2
spring.datasource.dbcp2.default-transaction-isolation=2
```





































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









