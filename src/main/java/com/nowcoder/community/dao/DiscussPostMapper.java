package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community.dao
 * @Author: zhangguoliu
 * @CreateTime: 2023-04-15  13:26
 * @Description: TODO
 * @Version: 1.0
 */
@Mapper
public interface DiscussPostMapper {
    /**
     * @description: 查询所有帖子，使用动态 SQL。用于开发社区首页和开发个人主页
     * @author: zhangguoliu
     * @date: 2023/4/15 13:37
     * @param: userId   开发个人主页需要的参数
     * @param: offset   每页起始帖子偏移量。社区首页分页显示需要的参数
     * @param: limit    每页显示帖子数量。社区首页分页显示需要的参数
     * @return: java.util.List<com.nowcoder.community.entity.DiscussPost>
     **/
    List<DiscussPost> selectDiscussPosts(
            @Param("userId") int userId,
            @Param("offset") int offset,
            @Param("limit") int limit);

    /**
     * @description: 查询所有帖子的数量，可用于社区首页分页显示，也可用于个人主页
     * @author: zhangguoliu
     * @date: 2023/4/15 13:42
     * @param: userId   开发个人主页需要的参数
     * @return: int
     **/
    int selectDiscussPostRows(int userId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(int id);
}
