package spring.failure.annlyzer;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringFailureAnalyzerApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(SpringFailureAnalyzerApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        throw new MyException("我就是要跑出个异常");
    }
}
