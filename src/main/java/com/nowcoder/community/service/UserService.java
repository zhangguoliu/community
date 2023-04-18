package com.nowcoder.community.service;

import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community.service
 * @Author: zhangguoliu
 * @CreateTime: 2023-04-15  14:50
 * @Description: TODO   注册逻辑，发送激活邮件
 * @Version: 1.0
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    // ${} 指用表达式的方式取 key
    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空！");
        } else {

            // 1.判断表单提交的必要信息是否为空
            if (StringUtils.isBlank(user.getUsername())) {
                map.put("usernameMsg", "账号不能为空！");
                return map;
            }
            if (StringUtils.isBlank(user.getPassword())) {
                map.put("passwordMsg", "密码不能为空！");
                return map;
            }
            if (StringUtils.isBlank(user.getEmail())) {
                map.put("emailMsg", "邮箱不能为空！");
                return map;
            }

            // 2.查询数据库，验证账号、邮箱是否已存在
            User u = userMapper.selectByName(user.getUsername());
            if (u != null) {
                map.put("usernameMsg", "该账号已存在!");
                return map;
            }
            u = userMapper.selectByEmail(user.getEmail());
            if (u != null) {
                map.put("emailMsg", "该邮箱已注册!");
                return map;
            }

            // 注册用户（插入数据库）
            user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
            user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
            user.setType(0);
            user.setStatus(0);
            user.setActivationCode(CommunityUtil.generateUUID());   // 激活码即随机字符串
            user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",
                    new Random().nextInt(1000)));   // String.format。占位符
            user.setCreateTime(new Date());
            userMapper.insertUser(user);

            // 提供 Model 数据，用 org.thymeleaf.context.Context 封装
            // 数据供 activation.html 使用
            Context context = new Context();
            context.setVariable("email", user.getEmail());

            // 拼激活链接，user.getId() 和 user.getActivationCode() 以示区分
            context.setVariable("url", domain + contextPath +
                    "/activation/" + user.getId() + "/" + user.getActivationCode());

            // 通过模板引擎生成邮件内容，邮件的内容为网页
            String content = templateEngine.process("/mail/activation", context);

            // 发送激活邮件
            mailClient.sendMail(user.getEmail(), "激活账户", content);
        }
        return map; // map 为空（null）表示没有问题
    }
}
