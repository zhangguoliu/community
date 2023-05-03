package com.nowcoder.community.service;

import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community.service
 * @Author: zhangguoliu
 * @CreateTime: 2023-05-03  16:02
 * @Description: TODO 关注、取消关注的数据和业务层
 * @Version: 1.0
 */

@Service
public class FollowService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 关注、取消关注
    public void followOrUnfollow(int userId, int entityType, int entityId) {
        String followeeKey = RedisKeyUtil.getFolloweeEntityKey(userId, entityType);
        String followerKey = RedisKeyUtil.getFollowerEntityKey(entityType, entityId);

        // 查询应在事务之外
        Double score = redisTemplate.opsForZSet().score(followeeKey, entityId);

        // 关注或者取关
        redisTemplate.execute(new SessionCallback<>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                if (score == null) {
                    operations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                    operations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());
                } else {
                    operations.opsForZSet().remove(followeeKey, entityId);
                    operations.opsForZSet().remove(followerKey, userId);
                }
                return operations.exec();
            }
        });
    }

    // 获取关注了某实体的数量
    public long getFolloweeEntityCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeEntityKey(userId, entityType);
        Long followeeCount = redisTemplate.opsForZSet().zCard(followeeKey);
        return followeeCount == null ? 0 : followeeCount;
    }

    // 获取某实体的粉丝的数量
    public long getFollowerEntityCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerEntityKey(entityType, entityId);
        Long followerCount = redisTemplate.opsForZSet().zCard(followerKey);
        return followerCount == null ? 0 : followerCount;
    }

    // 获取当前用户对某实体的关注状态
    public boolean getFollowStatus(int userId, int entityType, int entityId) {
        String followeeKey = RedisKeyUtil.getFolloweeEntityKey(userId, entityType);
        Double score = redisTemplate.opsForZSet().score(followeeKey, entityId);
        return score != null;
    }

    // 获取某实体的关注人数
    public int getFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeEntityKey(userId, entityType);
        Long followeeCount = redisTemplate.opsForZSet().zCard(followeeKey);
        return followeeCount.intValue();
    }

    // 获取某实体的关注列表
    public List<Map<String, Object>> getFolloweeList(int userId, int entityType, int offset, int limit) {
        String followeeKey = RedisKeyUtil.getFolloweeEntityKey(userId, entityType);

        Set<Object> userIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);
        if (userIds == null) {
            return null;
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (Object id : userIds) {
            int uId = (int) id;
            Map<String, Object> map = new HashMap<>();
            map.put("userId", uId);
            Double score = redisTemplate.opsForZSet().score(followeeKey, uId);
            map.put("followTime", new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }

    // 获取某实体的粉丝人数
    public int getFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerEntityKey(entityType, entityId);
        Long followerCount = redisTemplate.opsForZSet().zCard(followerKey);
        return followerCount.intValue();
    }

    // 获取某实体的关注列表
    public List<Map<String, Object>> getFollowerList(int entityType, int entityId, int offset, int limit) {
        String followerKey = RedisKeyUtil.getFollowerEntityKey(entityType, entityId);

        Set<Object> userIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);
        if (userIds == null) {
            return null;
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (Object id : userIds) {
            int uId = (int) id;
            Map<String, Object> map = new HashMap<>();
            map.put("userId", uId);
            Double score = redisTemplate.opsForZSet().score(followerKey, uId);
            map.put("followTime", new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }

}
