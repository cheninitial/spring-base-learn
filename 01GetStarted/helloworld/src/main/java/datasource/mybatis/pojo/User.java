package datasource.mybatis.pojo;

import datasource.enumeration.SexEnum;
import lombok.Data;
import org.apache.ibatis.type.Alias;

@Alias(value = "user")
@Data
public class User {

    private Long id;

    private String userName;

    private String note;

    private SexEnum sex = null;

}
