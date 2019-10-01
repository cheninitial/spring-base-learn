package datasource.jdbctemplete;

import datasource.enumeration.SexEnum;
import datasource.jdbctemplete.service.JdbcTmplUserService;
import datasource.pojo.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

//@EnableAutoConfiguration
//@ComponentScan("datasource.jdbctemplete")
@SpringBootApplication(scanBasePackages = "datasource.jdbctemplete")
public class JdbcTemplateTest {

    // 启动不了
//    public static void main(String[] args) {
//        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(JdbcTemplateTest.class);
//        JdbcTmplUserService jdbcTmplUserService = ctx.getBean(JdbcTmplUserService.class);
//
//        jdbcTmplUserService.insertUser(new User(null, "chen", SexEnum.MALE, "测试"));
//        List<User> userList = jdbcTmplUserService.findUsers("chen", "测试");
//        User user = userList.get(0);
//
//        Long userId = user.getId();
//        User update = new User(userId, "chen2", SexEnum.MALE, "测试2");
//        jdbcTmplUserService.updateUser(user);
//
//        User user1 = jdbcTmplUserService.getUser(userId);
//        System.out.println(user1);
//        User user2 = jdbcTmplUserService.getUser2(userId);
//        System.out.println(user2);
//        User user3 = jdbcTmplUserService.getUser3(userId);
//        System.out.println(user3);
//
//        jdbcTmplUserService.deleteUser(userId);
//
//
//    }

    public static void main(String[] args) {
        SpringApplication.run(JdbcTemplateTest.class, args);
    }

}
