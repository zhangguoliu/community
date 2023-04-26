package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
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
    // 生成验证码需要
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    // 生成验证码需要通过配置类注入第三方的 Bean
    @Autowired
    private Producer kaptchaProducer;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private MailClient mailClient;

    @Value("${server.servlet.context-path}")
    private String contextPath;

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

    // 生成验证码
    @GetMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response, HttpSession session) {
        // 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        // 将验证码存入 Session
        session.setAttribute("kaptcha", text);

        // 将图片输出给浏览器
        response.setContentType("image/png");
        try {
            ServletOutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("响应验证码失败：" + e.getMessage());
        }
    }

    /*
     * @description: 处理登录请求（POST）
     * @author: zhangguoliu
     * @date: 2023/4/24 14:12
     * @param: username
     * @param: password
     * @param: code 用户提交验证码
     * @param: rememberMe   是否记住我
     * @param: session 获取正确的验证码
     * @param: response 返回 Cookie 记住 ticket
     * @return: java.lang.String
     **/
    @PostMapping("/login")
    public String login(
            String username, String password,
            String code, boolean rememberMe,
            HttpSession session, HttpServletResponse response,
            Model model) {

        // 判断验证码（equalsIgnoreCase：不区分大小写）
        String kaptcha = (String) session.getAttribute("kaptcha");
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code)
                || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "验证码不正确！");
            return "/site/login";
        }

        // 检查账号、密码
        int expiredSeconds = rememberMe ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if (map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        } else {
            // 若 map 中没有则为 null，此时 model 得到后不会显示到页面
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }

    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/login";
    }

    // 忘记密码页面
    @GetMapping("/forget")
    public String getForgetPage() {
        return "/site/forget";
    }

    // 获取验证码
    @GetMapping("/forget/code")
    @ResponseBody
    public String getForgetCode(String email, HttpSession session) {
        if (StringUtils.isBlank(email)) {
            return CommunityUtil.getJSONString(1, "邮箱不能为空！");
        }

        User user = userService.findUserByEmail(email);
        if (user == null) {
            return CommunityUtil.getJSONString(2, "此邮箱尚未注册！");
        }

        // 发送邮件
        Context context = new Context();
        context.setVariable("email", email);
        String verifyCode = CommunityUtil.generateUUID().substring(0, 4);
        context.setVariable("verifyCode", verifyCode);
        String content = templateEngine.process("/mail/forget", context);
        mailClient.sendMail(email, "找回密码", content);

        // 保存验证码
        session.setAttribute("verifyCode", verifyCode);

        return CommunityUtil.getJSONString(0);
    }

    // 重置密码
    @PostMapping("/forget/password")
    public String resetPassword(String email, String verifycode, String password,
                                Model model, HttpSession session) {
        String code = (String) session.getAttribute("verifyCode");
        if (StringUtils.isBlank(verifycode) || StringUtils.isBlank(code)
                || !verifycode.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "验证码错误！");
            return "/site/forget";
        }

        Map<String, Object> map = userService.resetPassword(email, password);
        if (map.containsKey("user")) {
            return "redirect:/login";
        } else {
            model.addAttribute("emailMsg", map.get("emailMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/forget";
        }
    }
}
