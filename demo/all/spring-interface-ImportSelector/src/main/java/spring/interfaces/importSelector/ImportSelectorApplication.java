package spring.interfaces.importSelector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
//@Import({MyImportSelector1.class, MyImportSelector2.class, MyImportSelector3.class})
public class ImportSelectorApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(ImportSelectorApplication.class, args);
    }

    @Autowired
    private MyImportSelector1Bean myImportSelector1Bean;

    @Autowired
    private MyImportSelector2Bean myImportSelector2Bean;

    @Autowired
    private MyImportSelector3Bean myImportSelector3Bean;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("ApplicationRunner.run");
        System.out.println(myImportSelector1Bean);
        System.out.println(myImportSelector2Bean);
        System.out.println(myImportSelector3Bean);
    }
}
