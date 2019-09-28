package model.proxy.cglib;

import net.sf.cglib.proxy.Enhancer;

public class Test {
    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HelloServiceImpl2.class);
        enhancer.setCallback(new CglibMethodInterceptor());

        HelloServiceImpl2 proxy = (HelloServiceImpl2) enhancer.create();
        proxy.hello();
    }
}
