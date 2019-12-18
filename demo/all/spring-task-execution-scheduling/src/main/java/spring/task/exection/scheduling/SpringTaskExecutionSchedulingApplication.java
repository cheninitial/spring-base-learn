package spring.task.exection.scheduling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class SpringTaskExecutionSchedulingApplication implements CommandLineRunner {

    @Autowired
    private MyExecution myExecution;

    public static void main(String[] args) {
        SpringApplication.run(SpringTaskExecutionSchedulingApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        myExecution.run("1");
        myExecution.run("2");
        myExecution.run("3");
        myExecution.run("4");
        myExecution.run("5");
    }
}
