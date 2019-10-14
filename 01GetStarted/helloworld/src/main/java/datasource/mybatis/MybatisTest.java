package datasource.mybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("datasource.mybatis.dao")
public class MybatisTest {

    public static void main(String[] args) {
        SpringApplication.run(MybatisTest.class, args);
    }

}
