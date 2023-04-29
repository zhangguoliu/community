package com.nowcoder.community.util;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community.util
 * @Author: zhangguoliu
 * @CreateTime: 2023-04-21  10:24
 * @Description: TODO   community 常数
 * @Version: 1.0
 */
public interface CommunityConstant {
    // 邮件激活
    // 激活成功
    int ACTIVATION_SUCCESS = 0;

    // 重复激活
    int ACTIVATION_REPEAT = 1;

    // 激活失败
    int ACTIVATION_FAILURE = 2;

    // 登录凭证失效时间
    // 默认状态的登录凭证的超时时间（12 小时）
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    // 记住我状态的登录凭证的超时时间（100 天）
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;

    // 实体类型：帖子
    int ENTITY_TYPE_POST = 1;

    // 实体类型：评论
    int ENTITY_TYPE_COMMENT = 2;
}
