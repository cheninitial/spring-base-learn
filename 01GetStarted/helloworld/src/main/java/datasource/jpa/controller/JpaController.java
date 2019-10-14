package datasource.jpa.controller;

import datasource.jpa.dao.JpaUserRepository;
import datasource.jpa.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/jpa")
public class JpaController {

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @RequestMapping("/getUser")
    @ResponseBody
    public User getUser(Long id) {
        User user = jpaUserRepository.findById(id).get();
        return user;
    }

}
