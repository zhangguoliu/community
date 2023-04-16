package com.nowcoder.community.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community.util
 * @Author: zhangguoliu
 * @CreateTime: 2023-04-16  16:35
 * @Description: TODO 提供发送邮件的功能，但实际是委托给新浪去发送的。（即客户端）
 * @Version: 1.0
 */
@Component
public class MailClient {

    // logger 的名称是 MailClient
    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    // 用于发送邮件
    @Autowired
    private JavaMailSender javaMailSender;

    // 通过注解获取配置文件中的发件人
    // 注意 $ 符号
    @Value("${spring.mail.username}")
    private String from;

    public void sendMail(String to, String subject, String text) {
        // MimeMessage 封装邮件信息
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        // MimeMessageHelper 帮助类帮助设置 MimeMessage 的相关信息
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        try {
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);

            // 第二个参数为 true
            mimeMessageHelper.setText(text, true);

            // 通过 MimeMessageHelper 获取写好的邮件 MimeMessage
            MimeMessage message = mimeMessageHelper.getMimeMessage();

            // 发送邮件
            javaMailSender.send(message);
        } catch (MessagingException e) {
            logger.error("邮件发送失败：" + e.getMessage());
        }
    }
}
