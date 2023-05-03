package com.nowcoder.community.service;

import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community.service
 * @Author: zhangguoliu
 * @CreateTime: 2023-05-02  21:27
 * @Description: TODO 点赞功能的业务和数据层
 * @Version: 1.0
 */

@Service
public class LikeService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 点赞
    /*public void like(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        BoundSetOperations<String, Object> setOps = redisTemplate.boundSetOps(entityLikeKey);

        if (setOps.isMember(userId)) {
            setOps.remove(userId);
        } else {
            setOps.add(userId);
        }
    }*/

    // 重构点赞功能（事务：给某实体点赞 和 增减某人获得的点赞数量）
    public void like(int userId, int entityUserId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback<>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);

                // 查询要在事务之外
                Boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);

                operations.multi();
                if (isMember) {
                    operations.opsForSet().remove(entityLikeKey, userId);
                    operations.opsForValue().decrement(userLikeKey);
                } else {
                    operations.opsForSet().add(entityLikeKey, userId);
                    operations.opsForValue().increment(userLikeKey);
                }
                return operations.exec();
            }
        });
    }

    // 查询某实体点赞的数量
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        BoundSetOperations<String, Object> setOps = redisTemplate.boundSetOps(entityLikeKey);
        return setOps.size();
    }

    // 查询某人对某实体的点赞状态。用 int 而不是 boolean 是因为便于功能的扩展，如 -1 表示点踩
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        BoundSetOperations<String, Object> setOps = redisTemplate.boundSetOps(entityLikeKey);
        return setOps.isMember(userId) ? 1 : 0;
    }

    // 查询某个用户获得的赞数
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count;   // 注意 count 有可能为 null
    }
}
