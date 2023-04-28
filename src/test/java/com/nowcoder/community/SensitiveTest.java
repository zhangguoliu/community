package com.nowcoder.community;

import com.nowcoder.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community
 * @Author: zhangguoliu
 * @CreateTime: 2023-04-28  11:44
 * @Description: TODO
 * @Version: 1.0
 */
@SpringBootTest
// 使用和 CommunityApplication 一样的配置类
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTest {
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter() {
        String text = "这里可以吸毒，可以嫖娼，可以赌博，可以开票，可以色情耶";
        String filter = sensitiveFilter.filter(text);
        System.out.println(filter);

        text = "这里可以☆☆吸☆☆毒☆☆，可以☆☆嫖☆☆娼☆☆，可以☆☆赌☆☆博☆☆，" +
                "可以☆☆开☆☆票☆☆，可以☆☆色☆☆情☆☆耶☆☆";
        filter = sensitiveFilter.filter(text);
        System.out.println(filter);
    }
}
