package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CookieUtil;
import com.nowcoder.community.util.HostHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community.controller.interceptor
 * @Author: zhangguoliu
 * @CreateTime: 2023-04-24  21:36
 * @Description: TODO
 * @Version: 1.0
 */
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从 Cookie 中获取凭证
        String ticket = CookieUtil.getValue(request, "ticket");
        if (ticket != null) {

            // 查询凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);

            // 查询凭证是否有效
            if (loginTicket != null && loginTicket.getStatus() == 0
                    && loginTicket.getExpired().after(new Date())) {

                // 根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());

                // *在本次请求中持有用户 （需要暂存 user）
                /* 使用公共的类？放到容器中？不可行！浏览器访问服务器的请求是多对一的，是并发的，每个浏览器访问服务器，
                服务器都会创建一个线程来处理这个请求，因此服务器在处理请求的时候是一个多线程的环境 */
                /* 需要考虑线程的隔离，每个线程单独存一份 user，使用工具 ThreadLocal （封装在 HostHolder 中） */
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
