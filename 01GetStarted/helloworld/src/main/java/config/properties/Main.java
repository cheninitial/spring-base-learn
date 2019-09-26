package config.properties;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        PrintTest printTest = ctx.getBean(PrintTest.class);
        printTest.printConfigProperties();
        ctx.close();
    }
}
