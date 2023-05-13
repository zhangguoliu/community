package com.nowcoder.community.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community.aspect
 * @Author: zhangguoliu
 * @CreateTime: 2023-04-30  22:17
 * @Description: TODO 3.33 统一记录日志
 * @Version: 1.0
 */

@Component
@Aspect
public class ServiceLoggerAspect {

    private static final Logger logger = LoggerFactory.getLogger(ServiceLoggerAspect.class);

    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")
    public void pointcut() {
    }

    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {

        // 日志格式：用户（IP 地址）在何时访问了什么方法
        // 获取 IP 地址
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        // 如果是 kafka 的 EventProducer 调用了业务层，先不处理
        if (attributes == null) {
            return;
        }

        HttpServletRequest request = attributes.getRequest();
        String ip = request.getRemoteHost();

        // 获取当前时间
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // 获取类名和方法名
        String target = joinPoint.getSignature().getDeclaringTypeName()
                + "." + joinPoint.getSignature().getName();

        logger.info(String.format("用户[%s]，在[%s]，访问了[%s]", ip, now, target));
    }

}
