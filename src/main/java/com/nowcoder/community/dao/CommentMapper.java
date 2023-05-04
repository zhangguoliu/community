package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community.dao
 * @Author: zhangguoliu
 * @CreateTime: 2023-04-29  00:05
 * @Description: TODO
 * @Version: 1.0
 */
@Mapper
public interface CommentMapper {

    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    int selectCountByEntity(int entityType, int entityId);

    int insertComment(Comment comment);

    List<Comment> selectCommentsByUserId(int userId, int offset, int limit);

    int selectCountByUserId(int userId);

    int selectPostIdByCommentId(int commentForPostId);

}
