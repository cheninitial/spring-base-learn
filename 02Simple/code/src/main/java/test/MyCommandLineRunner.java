package test;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

//@Component
public class MyCommandLineRunner implements CommandLineRunner {


    @Override
    public void run(String... args) throws Exception {

        System.out.println("MyCommandLineRunner");
        if (args != null) {
            for (String arg : args) {
                System.out.println(arg);
            }
        }

    }
}
