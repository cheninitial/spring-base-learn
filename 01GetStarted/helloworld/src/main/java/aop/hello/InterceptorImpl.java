package aop.hello;

import java.lang.reflect.InvocationTargetException;

public class InterceptorImpl implements Interceptor {
    @Override
    public boolean before() {
        System.out.println("【InterceptorImpl】 调用了 before() 方法");
        return true;
    }

    @Override
    public void after() {
        System.out.println("【InterceptorImpl】 调用了 after() 方法");
    }

    @Override
    public Object around(Invocation invocation) throws InvocationTargetException, IllegalAccessException {
        System.out.println("【InterceptorImpl】 调用了 around() 方法之前");
        Object obj = invocation.processe();
        System.out.println("【InterceptorImpl】 调用了 around() 方法之后");

        return obj;
    }

    @Override
    public void afterReturning() {
        System.out.println("【InterceptorImpl】 调用了 afterReturning() 方法");
    }

    @Override
    public void afterThrowing() {
        System.out.println("【InterceptorImpl】 调用了 afterThrowing() 方法");
    }

    @Override
    public boolean useAround() {
        return true;
    }
}
