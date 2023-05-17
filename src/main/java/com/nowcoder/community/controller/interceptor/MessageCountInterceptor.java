package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.util.HostHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community.controller.interceptor
 * @Author: zhangguoliu
 * @CreateTime: 2023-05-14  17:50
 * @Description: TODO 总的未读消息数
 * @Version: 1.0
 */
@Component
public class MessageCountInterceptor implements HandlerInterceptor {
    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
            int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
            modelAndView.addObject("messageUnreadCount", letterUnreadCount + noticeUnreadCount);
        }
    }
}
