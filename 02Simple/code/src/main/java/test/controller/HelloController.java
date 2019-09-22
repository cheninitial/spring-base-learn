package test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import test.service.HelloService;
import test.service.impl.HelloServiceImpl;

@RestController
public class HelloController {

    @Autowired
    private HelloServiceImpl helloService;

    @RequestMapping("/hello")
    public String hello() {
        return helloService.hello();
    }


}
