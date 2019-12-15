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
@Data
public class BeanCreateByImport {

    private String name = "BeanCreateByImport";

}
