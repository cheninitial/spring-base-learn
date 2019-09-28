package aop.hello;

public class Main {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        HelloService proxy = (HelloService) ProxyBean.getProxyBean(helloService, new InterceptorImpl());
        proxy.hello("zhangsan");
        System.out.println("##########");
        proxy.hello(null);
    }
}
