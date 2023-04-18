package com.nowcoder.community.util;

import io.micrometer.common.util.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community.util
 * @Author: zhangguoliu
 * @CreateTime: 2023-04-18  15:08
 * @Description: TODO 加密工具
 * @Version: 1.0
 */
public class CommunityUtil {

    // 生成随机字符串
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    // MD5 加密。key 是加盐（salt）后的
    public static String md5(String key) {

        // 不处理 null、空串、空字符等
        if (StringUtils.isBlank(key)) {
            return null;
        }

        // 使用 Spring 自带的加密工具 DigestUtils
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
