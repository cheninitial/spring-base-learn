package spring.interfaces.importSelector;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.annotation.Annotation;

@Configuration
public class MyImportSelector2 implements ImportSelector, Ordered {

    @Override
    public int getOrder() {
        return 2;
    }

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        System.out.println("MyImportSelector2.selectImports() Order(2)");
        return new String[]{MyImportSelector2Bean.class.getName()};
    }
}
