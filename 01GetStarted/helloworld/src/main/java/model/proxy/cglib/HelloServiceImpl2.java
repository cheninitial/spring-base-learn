package model.proxy.cglib;

public class HelloServiceImpl2 {
    public String hello() {
        System.out.println("[HelloServiceImpl] 调用 hello() 方法");
        return "hello";
    }
}
