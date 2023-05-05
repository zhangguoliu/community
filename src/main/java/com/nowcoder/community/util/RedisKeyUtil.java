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
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_ENTITY_FOLLOWEE = "followee";
    private static final String PREFIX_ENTITY_FOLLOWER = "follower";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";

    // 某个实体的赞。like:entity:entityType:entityId → set(userId)
    // 使用 set 而不是用整数的意义是便于功能的扩展，如加入知道是谁点赞的功能
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    // 某个用户获得的赞
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    // 某个用户关注了某个实体。followee:userId:entityType -> zset(entityId, nowTime)
    public static String getFolloweeEntityKey(int userId, int entityType) {
        return PREFIX_ENTITY_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    // 某个实体被某个用户关注。follower:entityType:entityId -> zset(userId, nowTime)
    public static String getFollowerEntityKey(int entityType, int entityId) {
        return PREFIX_ENTITY_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    public static String getKaptchaKey(String uuid) {
        return PREFIX_KAPTCHA + SPLIT + uuid;
    }

    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }
}
