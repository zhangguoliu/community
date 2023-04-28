package com.nowcoder.community;

import com.nowcoder.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community
 * @Author: zhangguoliu
 * @CreateTime: 2023-04-28  22:05
 * @Description: TODO
 * @Version: 1.0
 */
@SpringBootTest
// 使用和 CommunityApplication 一样的配置类
@ContextConfiguration(classes = CommunityApplication.class)
public class TransactionTests {

    @Autowired
    private AlphaService alphaService;

    @Test
    public void testSave1() {
        Object o = alphaService.save1();
        System.out.println(o);
    }

    @Test
    public void testSave2() {
        Object o = alphaService.save2();
        System.out.println(o);
    }
}