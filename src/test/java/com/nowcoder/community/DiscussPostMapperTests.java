package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community
 * @Author: zhangguoliu
 * @CreateTime: 2023-04-15  13:57
 * @Description: TODO
 * @Version: 1.0
 */
@SpringBootTest
// 使用和 CommunityApplication 一样的配置类
@ContextConfiguration(classes = CommunityApplication.class)
public class DiscussPostMapperTests {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testSelect() {
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(149, 0, 10);
        for (DiscussPost discussPost : discussPosts) {
            System.out.println(discussPost);
        }

        int i = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(i);
    }

    @Test
    public void testInsert() {
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(1);
        discussPost.setTitle("Test");
        discussPost.setContent("Test");
        discussPost.setCreateTime(new Date());
        int i = discussPostMapper.insertDiscussPost(discussPost);
        System.out.println(i);
    }
}
