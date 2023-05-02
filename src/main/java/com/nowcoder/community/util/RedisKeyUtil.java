package com.nowcoder.community.util;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community.util
 * @Author: zhangguoliu
 * @CreateTime: 2023-05-02  21:02
 * @Description: TODO 拼存储某个实体的点赞信息的 Redis 的 key
 * @Version: 1.0
 */
public class RedisKeyUtil {
    private static final String SPLIT = ":";

    private static final String PREFIX_ENTITY_LIKE = "like:entity";

    // 某个实体的赞。like:entity:entityType:entityId → set(userId)
    // 使用 set 而不是用整数的意义是便于功能的扩展，如加入知道是谁点赞的功能
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }
}
