package com.nowcoder.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community.annotation
 * @Author: zhangguoliu
 * @CreateTime: 2023-04-26  19:02
 * @Description: TODO 是否需要在登录状态下请求
 * @Version: 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequired {
}
