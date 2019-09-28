package aop.hello;

import org.springframework.util.StringUtils;

public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String name) {

        if (StringUtils.isEmpty(name)) {
            throw new RuntimeException("【HelloServiceImpl】 调用 hello() 方法发生异常");
        }

        System.out.println("【HelloServiceImpl】 调用 hello() 方法");
        return "hello";
    }
}
