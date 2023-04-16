package com.nowcoder.community.service;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
