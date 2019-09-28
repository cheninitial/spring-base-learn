package aop.hello;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Invocation {
    private Object[] params;
    private Object target;
    private Method method;

    public Invocation(Object[] params, Object target, Method method) {
        this.params = params;
        this.target = target;
        this.method = method;
    }

    public Object processe() throws InvocationTargetException, IllegalAccessException {
        return method.invoke(target, params);
    }
}
