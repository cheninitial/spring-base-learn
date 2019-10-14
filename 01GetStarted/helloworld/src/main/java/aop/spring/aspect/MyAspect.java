package aop.spring.aspect;

import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class MyAspect {

    static {
        System.out.println("【MyAspect】 加载");
    }

    @Pointcut("execution(* aop.spring.service.UserServiceImpl.printUser(..))")
    public void pointCut() {
        System.out.println("[MyAspect] 调用 pointCut() 方法");
    }

    @Before("pointCut()")
    public void before() {
        System.out.println("[MyAspect] 调用 before() 方法");
    }

    @After("pointCut()")
    public void After() {
        System.out.println("[MyAspect] 调用 After() 方法");
    }

    @AfterReturning("pointCut()")
    public void AfterReturning() {
        System.out.println("[MyAspect] 调用 AfterReturning() 方法");
    }

    @AfterThrowing("pointCut()")
    public void AfterThrowing() {
        System.out.println("[MyAspect] 调用 AfterThrowing() 方法");
    }
}
