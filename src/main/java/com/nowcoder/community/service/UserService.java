package com.nowcoder.community.service;

import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import com.nowcoder.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community.service
 * @Author: zhangguoliu
 * @CreateTime: 2023-04-15  14:50
 * @Description: TODO   注册逻辑，发送激活邮件
 * @Version: 1.0
 */

// 需要激活邮件的业务，所以实现 CommunityConstant
@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    /*@Autowired
    private LoginTicketMapper loginTicketMapper;*/

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

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

    // 激活邮件的业务，需要实现 CommunityConstant
    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    /*
     * @description: 登录验证的逻辑（账号、密码等）
     * @author: zhangguoliu
     * @date: 2023/4/24 13:38
     * @param: username
     * @param: password
     * @param: expiredSeconds 凭证的失效时间
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     **/
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }

        // 验证账号
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在！");
            return map;
        }

        // 验证状态
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活！");
            return map;
        }

        // 验证密码
        String pwd = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(pwd)) {
            map.put("passwordMsg", "密码不正确！");
            return map;
        }

        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000L));
        /*loginTicketMapper.insertLoginTicket(loginTicket);*/
        String ticketKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(ticketKey, loginTicket, expiredSeconds, TimeUnit.SECONDS);

        map.put("ticket", loginTicket.getTicket());

        return map;
    }

    // 登出
    public void logout(String ticket) {
        /*loginTicketMapper.updateStatus(ticket, 1);*/

        // 暂且先删除。如果后面需要统计登录历史信息，则取消删除，替换为修改状态
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        redisTemplate.delete(ticketKey);
    }

    public LoginTicket findLoginTicket(String ticket) {
        /*return loginTicketMapper.selectByTicket(ticket);*/
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
    }

    public void updateHeader(int userId, String headerUrl) {
        userMapper.updateHeader(userId, headerUrl);
    }

    public User findUserByEmail(String email) {
        return userMapper.selectByEmail(email);
    }

    // 重置密码
    public Map<String, Object> resetPassword(String email, String password) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(email)) {
            map.put("emailMsg", "邮箱不能为空！");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }

        // 验证邮箱
        User user = userMapper.selectByEmail(email);
        if (user == null) {
            map.put("emailMsg", "此邮箱尚未注册！");
            return map;
        }

        // 重置密码
        password = CommunityUtil.md5(password + user.getSalt());
        userMapper.updatePassword(user.getId(), password);
        map.put("user", user);
        return map;
    }

    // 修改密码
    public Map<String, Object> updatePassword(int userId, String oldPassword, String newPassword) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(oldPassword)) {
            map.put("oldPasswordMsg", "原密码不能为空！");
            return map;
        }
        if (StringUtils.isBlank(newPassword)) {
            map.put("newPasswordMsg", "新密码不能为空");
            return map;
        }

        // 验证原始密码
        User user = userMapper.selectById(userId);
        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        if (!user.getPassword().equals(oldPassword)) {
            map.put("oldPasswordMsg", "原密码输入有误！");
            return map;
        }

        // 更新密码
        newPassword = newPassword + user.getSalt();
        userMapper.updatePassword(userId, CommunityUtil.md5(newPassword));

        return map;
    }

    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }
}
