package aop.hello;


import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyCglibBean implements MethodInterceptor {

    private Object target = null;

    private Interceptor interceptor = null;

    public static Object getProxyBean(Object target, Interceptor interceptor) {
        ProxyCglibBean proxyBean = new ProxyCglibBean();
        proxyBean.target = target;
        proxyBean.interceptor = interceptor;

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());

        enhancer.setCallback(proxyBean);

        Object proxy = enhancer.create();
        return proxy;
    }


    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        boolean exceptionFlag = false;
        Invocation invocation = new Invocation(args, target, method);
        this.interceptor.before();
        Object retObj = null;
        try {
            if (this.interceptor.useAround()) {
                retObj = this.interceptor.around(invocation);
            } else {
                retObj = method.invoke(target, args);
            }
        } catch (Exception ex) {
            exceptionFlag = true;
        }

        this.interceptor.after();
        if (exceptionFlag) {
            this.interceptor.afterThrowing();
        } else {
            this.interceptor.afterReturning();
            return retObj;
        }

        return null;
    }
}
