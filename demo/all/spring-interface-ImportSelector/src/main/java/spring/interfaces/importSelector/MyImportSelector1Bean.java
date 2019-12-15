package spring.interfaces.importSelector;

import lombok.Data;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

@Data
public class MyImportSelector1Bean {

    private String name = "MyImportSelector1Bean";

}
