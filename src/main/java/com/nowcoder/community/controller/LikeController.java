package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community.controller
 * @Author: zhangguoliu
 * @CreateTime: 2023-05-02  22:00
 * @Description: TODO 点赞功能的表现层（异步请求）
 * @Version: 1.0
 */

@Controller
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @PostMapping("/like")
    @ResponseBody
    public String like(int entityUserId, int entityType, int entityId) {
        User user = hostHolder.getUser();

        // 判断用户是否登录：可以通过注解 @LoginRequired 或者 将来用 Spring Security 进行重构

        // 点赞
        likeService.like(user.getId(), entityUserId, entityType, entityId);

        // 点赞的数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);

        // 点赞的状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);

        // 点赞的结果
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);
        return CommunityUtil.getJSONString(0, null, map);
    }

}
