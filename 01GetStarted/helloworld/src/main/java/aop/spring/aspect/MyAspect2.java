package aop.spring.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class MyAspect2 {

    static {
        System.out.println("【MyAspect2】 加载");
    }

    @Pointcut("@annotation(aop.spring.aspect.AspectAnnotation)")
    public void pointCut() {
        System.out.println("[MyAspect2] 调用 pointCut() 方法");
    }

    @Before("pointCut() && args(name2)")
    public void before(
            JoinPoint joinPoint, String name2
    ) {
        System.out.println("[MyAspect2] 调用 before() 方法, name参数为： " + name2);
    }

    @After("pointCut() && @args()")
    public void After() {
        System.out.println("[MyAspect2] 调用 After() 方法");
    }

    @AfterReturning("pointCut()")
    public void AfterReturning() {
        System.out.println("[MyAspect2] 调用 AfterReturning() 方法");
    }

    @AfterThrowing("pointCut()")
    public void AfterThrowing() {
        System.out.println("[MyAspect2] 调用 AfterThrowing() 方法");
    }

    @Around("pointCut()")
    public void arroud(ProceedingJoinPoint joinPoint) throws Throwable{
        System.out.println("[MyAspect2] 调用 arroud() 方法， 前");
        joinPoint.proceed();
        System.out.println("[MyAspect2] 调用 arroud() 方法， 后");
    }

}
