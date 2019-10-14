package datasource.mybatis.controller;

import datasource.mybatis.dao.MybatisUserDao;
import datasource.mybatis.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/mybatis")
public class MybatisController {

    @Autowired
    private MybatisUserDao mybatisUserDao;

    @RequestMapping("/getUser")
    @ResponseBody
    public User getUser(Long id) {
        return mybatisUserDao.getUser(id);
    }

}
