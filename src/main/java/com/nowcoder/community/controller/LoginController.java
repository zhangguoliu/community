package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community.controller
 * @Author: zhangguoliu
 * @CreateTime: 2023-04-16  20:37
 * @Description: TODO
 * @Version: 1.0
 */

// 需要激活邮件的业务，所以实现 CommunityConstant
@Controller
public class LoginController implements CommunityConstant {
    @Autowired
    private UserService userService;

    // 访问注册页面倒是无须 Model
    @GetMapping("/register")
    public String getRegisterPage() {
        return "/site/register";
    }

    // 注册的逻辑
    // 路径一样，但提交方式不同
    @PostMapping("/register")
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);

        if (map == null || map.isEmpty()) {

            // map 为空（null）表示没有问题，注册成功，通过 operate-result.html 实现跳转
            model.addAttribute("msg",
                    "注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快激活");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {

            // 注册数据有问题时，将错误消息发送回注册页面
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }

    // 激活的逻辑
    // http://localhos:8080/community/activation/159/1d106f3c6a6949aaa19bdab7e46ce04e
    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model,
                             @PathVariable("userId") int userId,
                             @PathVariable("code") String code) {
        int result = userService.activation(userId, code);
        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg",
                    "激活成功，您的账号已经可以正常使用了！");
            model.addAttribute("target", "/login");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg",
                    "无效操作，该账号已经激活过了！");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg",
                    "激活失败，您提供的激活码不正确！");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    // 获取登录页面
    @GetMapping("/login")
    public String getLoginPage() {
        return "/site/login";
    }
}
