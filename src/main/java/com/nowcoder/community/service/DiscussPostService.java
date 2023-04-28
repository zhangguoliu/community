package com.nowcoder.community.service;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community.service
 * @Author: zhangguoliu
 * @CreateTime: 2023-04-15  14:41
 * @Description: TODO
 * @Version: 1.0
 */
@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    /**
     * @description: 显示帖子的时候，不会显示 userId，而是显示 userId 对应的姓名头像等
     * 解决的方法有两个 一是通过 SQL 语句联接查询。二是通过业务逻辑处理
     * @author: zhangguoliu
     * @date: 2023/4/15 14:44
     * @param: userId
     * @param: offset
     * @param: limit
     * @return: java.util.List<com.nowcoder.community.entity.DiscussPost>
     **/
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    public int findDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    public int addDiscussPost(DiscussPost post) {
        if (post == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }

        // 转义 HTML 标记
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));

        // 过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }
}
