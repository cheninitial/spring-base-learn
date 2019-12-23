package spring.bean.autowired;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBeanAutowiredApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(SpringBeanAutowiredApplication.class, args);
    }

    @Autowired
    private AutowiredInFieldBean autowiredInFieldBean;

    @Autowired
    private AutowiredInConstructorMethodBean autowiredInConstructorMethodBean;

    @Autowired
    private AutowiredInConstructorParamBean autowiredInConstructorParamBean;

    @Autowired
    private AutowiredNoInConstructorBean autowiredNoInConstructorBean;

    @Autowired
    private AutowiredInSetterBean autowiredInSetterBean;

    @Autowired
    private AutowiredInConfigMethodBean autowiredInConfigMethodBean;

    @Autowired
    private AutowiredOrderTest autowiredOrderTest;

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        System.out.println(String.format("autowiredInFieldBean: %s", autowiredInFieldBean.say()));
//        System.out.println(String.format("autowiredInConstructorMethodBean: %s", autowiredInConstructorMethodBean.say()));
//        System.out.println(String.format("autowiredInConstructorParamBean: %s", autowiredInConstructorParamBean.say()));
//        System.out.println(String.format("autowiredNoInConstructorBean: %s", autowiredNoInConstructorBean.say()));
//        System.out.println(String.format("autowiredInSetterBean: %s", autowiredInSetterBean.say()));
//        System.out.println(String.format("autowiredInConfigMethodBean: %s", autowiredInConfigMethodBean.say()));

        System.out.println(autowiredOrderTest.say());
    }
}
