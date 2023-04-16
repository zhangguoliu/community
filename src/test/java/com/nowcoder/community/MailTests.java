package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community
 * @Author: zhangguoliu
 * @CreateTime: 2023-04-16  17:34
 * @Description: TODO 测试 MailClient 发送邮件
 * @Version: 1.0
 */
@SpringBootTest
// 使用和 CommunityApplication 一样的配置类
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {

    @Autowired
    private MailClient mailClient;

    // 测试发送文本邮件
    @Test
    public void testTextMail() {
        mailClient.sendMail("1664730584@qq.com", "testTextMail", "testTextMail");
    }

    // 在测试类中，没有 @Controller，需要注入 TemplateEngine
    @Autowired
    private TemplateEngine templateEngine;

    // 测试发送 HTML 邮件
    @Test
    public void testHTMLMail() {
        // org.thymeleaf.context.Context，继承自 AbstractContext
        Context context = new Context();

        // 而AbstractContext 有 setVariable 方法，可以传递携带 Model 数据
        context.setVariable("username", "张三");

        // Model 数据：context     模板路径：/mail/demo
        String html = templateEngine.process("/mail/demo", context);
        System.out.println(html);

        mailClient.sendMail("1664730584@qq.com", "testHTMLMail", html);
    }
}
