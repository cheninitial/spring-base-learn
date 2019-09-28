package model.proxy;

public class HelloServiceImpl implements HelloService {
    @Override
    public String hello() {
        System.out.println("[HelloServiceImpl] 调用 hello() 方法");
        return "hello";
    }
}
