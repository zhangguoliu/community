package com.nowcoder.community;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.entity.LoginTicket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community
 * @Author: zhangguoliu
 * @CreateTime: 2023-04-24  10:45
 * @Description: TODO
 * @Version: 1.0
 */
@SpringBootTest
// 使用和 CommunityApplication 一样的配置类
@ContextConfiguration(classes = CommunityApplication.class)
public class LoginTicketMapperTest {
    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    public void testInsert() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(0);
        loginTicket.setTicket("test");
        loginTicket.setStatus(0);

        // 10 分钟后失效
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));
        int i = loginTicketMapper.insertLoginTicket(loginTicket);
        System.out.println(i);
    }

    @Test
    public void testSelect() {
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("test");
        System.out.println(loginTicket);
    }

    @Test
    public void testUpdate() {
        int i = loginTicketMapper.updateStatus("test", 1);
        System.out.println(i);
    }
}
