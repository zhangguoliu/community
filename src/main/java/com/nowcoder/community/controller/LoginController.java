package com.nowcoder.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community.controller
 * @Author: zhangguoliu
 * @CreateTime: 2023-04-16  20:37
 * @Description: TODO
 * @Version: 1.0
 */
@Controller
public class LoginController {
    // 访问注册页面倒是无须 Model
    @GetMapping("/register")
    public String register() {
        return "/site/register";
    }
}
