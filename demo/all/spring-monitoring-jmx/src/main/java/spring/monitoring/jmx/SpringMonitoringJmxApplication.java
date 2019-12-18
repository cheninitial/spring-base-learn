package spring.monitoring.jmx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jmx.support.ConnectorServerFactoryBean;
import org.springframework.remoting.rmi.RmiRegistryFactoryBean;

@SpringBootApplication
public class SpringMonitoringJmxApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringMonitoringJmxApplication.class, args);
    }

    @Bean
    public RmiRegistryFactoryBean rmiRegistryFactoryBean() {
        final RmiRegistryFactoryBean rmiRegistryFactoryBean = new RmiRegistryFactoryBean();
        rmiRegistryFactoryBean.setPort(8896);
        rmiRegistryFactoryBean.setAlwaysCreate(true);
        return rmiRegistryFactoryBean;
    }

    @Bean
    @DependsOn("rmiRegistryFactoryBean")
    public ConnectorServerFactoryBean connectorServerFactoryBean() throws Exception {
        final ConnectorServerFactoryBean connectorServerFactoryBean =new ConnectorServerFactoryBean();

        connectorServerFactoryBean.setObjectName("connector:name=rmi");

        String serviceUrl = String.format("service:jmx:rmi://%s:%s/jndi/rmi://%s:%s/jmxrmi", "127.0.0.1", 8896, "127.0.0.1", 8896);

        connectorServerFactoryBean.setServiceUrl(serviceUrl);

        System.out.println(("ConnectorServerFactoryBean create success !! url: " + serviceUrl));

        return connectorServerFactoryBean;
    }

}
