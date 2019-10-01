package datasource.jdbctemplete.service;


import datasource.enumeration.SexEnum;
import datasource.jdbctemplete.JdbcTemplateTest;
import datasource.pojo.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JdbcTemplateTest.class)
public class JdbcTmplUserServiceImplTest {

    @Autowired
    private JdbcTmplUserService jdbcTmplUserService;

    @Test
    public void case1() {


        jdbcTmplUserService.insertUser(new User(null, "chen", SexEnum.MALE, "测试"));
        List<User> userList = jdbcTmplUserService.findUsers("chen", "测试");
        User user = userList.get(0);

        Long userId = user.getId();
        User update = new User(userId, "chen2", SexEnum.MALE, "测试2");
        jdbcTmplUserService.updateUser(user);

        User user1 = jdbcTmplUserService.getUser(userId);
        System.out.println(user1);
        User user2 = jdbcTmplUserService.getUser2(userId);
        System.out.println(user2);
//        User user3 = jdbcTmplUserService.getUser3(userId);
//        System.out.println(user3);

        jdbcTmplUserService.deleteUser(userId);
    }

    @Test
    public void getUser() {

        Long userId = 6L;
        User user1 = jdbcTmplUserService.getUser(userId);
        System.out.println(user1);

    }

    @Test
    public void deletById() {
        jdbcTmplUserService.deleteUser(6L);
    }



}
