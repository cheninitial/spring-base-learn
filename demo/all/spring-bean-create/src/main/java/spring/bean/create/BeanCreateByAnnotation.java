package spring.bean.create;

import lombok.Data;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 通过注解生成Bean
 * </p>
 *
 * @author yuanliang.chen
 * @date 2019/12/13
 */
@Component
@Configuration
@Service
@Data
public class BeanCreateByAnnotation {

    private String name = "BeanCreateByAnnotation";

}
