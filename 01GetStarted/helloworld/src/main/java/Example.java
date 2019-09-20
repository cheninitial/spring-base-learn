import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
public class Example {

    @RequestMapping("/")
    String home() {
        return "Hello world";
    }

    @Value("${spring.application.name}")
    private String name;

    @RequestMapping("/application")
    ApplicationInfo application() {

        ApplicationInfo applicationInfo = new ApplicationInfo();
        applicationInfo.setName(name);

        return applicationInfo;
    }

    public static void main(String[] args) {
        SpringApplication.run(Example.class, args);
    }

}

class ApplicationInfo{
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
