package config.properties;

import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("config.properties")
@EnableConfigurationProperties(ConfigProperties.class)
public class AppConfig {

}
