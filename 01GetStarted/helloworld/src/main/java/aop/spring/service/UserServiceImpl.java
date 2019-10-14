package aop.spring.service;

import aop.spring.aspect.AspectAnnotation;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public void printUser(String name) {
        System.out.println("[UserServiceImpl] 调用 printUser() 方法");
        if (name == null) {
            throw new NullPointerException("[UserServiceImpl] 抛出异常");
        }
    }

    @Override
    @AspectAnnotation
    public void printUser2(String name) {
        System.out.println("[UserServiceImpl] 调用 printUser2() 方法");
        if (name == null) {
            throw new NullPointerException("[UserServiceImpl2] 抛出异常");
        }
    }
}
