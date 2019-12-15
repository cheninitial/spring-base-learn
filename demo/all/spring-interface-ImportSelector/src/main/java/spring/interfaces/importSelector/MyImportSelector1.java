package spring.interfaces.importSelector;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotationMetadata;

@Configuration
@Order(1)
public class MyImportSelector1 implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        System.out.println("MyImportSelector1.selectImports() Order(1)");
        return new String[]{MyImportSelector1Bean.class.getName()};
    }
}
