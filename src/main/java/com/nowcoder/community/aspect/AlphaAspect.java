package com.nowcoder.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community.aspect
 * @Author: zhangguoliu
 * @CreateTime: 2023-04-30  20:22
 * @Description: TODO 3.33 统一记录日志 （AOP 示例）
 * @Version: 1.0
 */

// 需要测试 AOP 时解除此处的两个注解
// @Component
// @Aspect  // 表示此类是一个方面组件
public class AlphaAspect {

    /* 切点 （public void ……
     * 定义切点（通过 @Pointcut 注解
     * 第一个 * 表示方法的返回值
     * 第二个 *.* 表示所有的业务组件的所有方法
     * (..) 表示所有的参数
     */
    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")
    public void pointcut() {
    }

    // 下面都是通知（共 5 种通知），除了 @Around 都是 public void …

    // 在连接点的之前织入逻辑……
    @Before("pointcut()")
    public void before() {
        System.out.println("before");
    }

    // 在连接点的之后织入逻辑……
    @After("pointcut()")
    public void after() {
        System.out.println("after");
    }

    // 在连接点有了返回值之后织入逻辑……
    @AfterReturning("pointcut()")
    public void afterReturning() {
        System.out.println("afterReturning");
    }

    // 在连接点抛异常之后织入逻辑……
    @AfterThrowing("pointcut()")
    public void afterThrowing() {
        System.out.println("afterThrowing");
    }

    /* 在连接点的前后都织入逻辑……
     * 方法需要有返回值、参数（连接点） ProceedingJoinPoint ……
     * 和抛出 Throwable 异常
     */
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("around before");
        Object obj = joinPoint.proceed();
        System.out.println("around after");
        return obj;
    }

}
