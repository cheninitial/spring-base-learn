package spring.interfaces.importSelector;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 不加上 Order 会出现错误
 * */
@Configuration
public class MyImportSelector3 implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        System.out.println("MyImportSelector3.selectImports() Order(null)");
        return new String[]{MyImportSelector3Bean.class.getName()};
    }
}
