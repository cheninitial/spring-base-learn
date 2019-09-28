package model.proxy;

import java.lang.reflect.Proxy;

public class Test {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        HelloService proxy =
                (HelloService) Proxy.newProxyInstance(
                        helloService.getClass().getClassLoader(),
                        helloService.getClass().getInterfaces(),
                        new MyInvocationHandler(helloService));
        proxy.hello();
    }
}
