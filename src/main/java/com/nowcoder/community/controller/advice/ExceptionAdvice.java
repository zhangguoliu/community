package com.nowcoder.community.controller.advice;

import com.nowcoder.community.util.CommunityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community.controller.advice
 * @Author: zhangguoliu
 * @CreateTime: 2023-04-30  17:04
 * @Description: TODO 3.31 统一处理异常
 * @Version: 1.0
 */

/* @ControllerAdvice：默认扫描所有的 Bean ，范围太大了
 * 修改为 @ControllerAdvice(annotations = Controller.class)：扫描所有带有 @Controller 注解的 Bean
 */
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    // 使用 Spring 解决方案的目的就是记录异常日志
    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    /* {Exception.class} 中指出要处理的异常的类型
     * 方法必须是 public void
     */
    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {

        // 记录异常的概括
        logger.error("服务器发生异常：" + e.getMessage());

        // 记录异常的详细
        for (StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }

        /* 判断请求是 普通请求 还是 异步请求，不同的请求的 返回 需要分别处理
         * 可以作为一个固定的通用的判断技巧
         */
        String xRequestedWith = request.getHeader("x-requested-with");

        // 如果是异步请求
        if ("XMLHttpRequest".equals(xRequestedWith)) {

            /* application/json
             * 表示返回的字符串会被浏览器 自动 转换为 JSON 对象
             * application/plain
             * 表示返回的字符串需要 手动 转换为 JSON 对象，也即 js 中的 data = $.parseJSON(data);
             */
            response.setContentType("application/plain;charset=utf-8");

            // 获取输出流来输出字符串。此处的 getWriter() 异常直接抛出去
            PrintWriter writer = response.getWriter();

            // 因为是 application/plain，所以需要 手动 保证是 JSON 格式的字符串
            writer.write(CommunityUtil.getJSONString(1, "服务器发生错误！"));
        }

        // 如果不是异步请求：重定向即可
        else {
            response.sendRedirect(request.getContextPath() + "/error");
        }

    }

}
