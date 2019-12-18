package spring.task.exection.scheduling;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class MyExecution {

    @Async
    public void run(String message) {
        System.out.println("MyExecution: " + message);
    }

}
