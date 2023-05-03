package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community.controller
 * @Author: zhangguoliu
 * @CreateTime: 2023-05-03  16:28
 * @Description: TODO 关注、取关的表现层（异步请求）
 * @Version: 1.0
 */

@Controller
public class FollowController implements CommunityConstant {

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @PostMapping("/followOrNot")
    @ResponseBody
    public String followOrUnfollow(int entityType, int entityId) {
        User user = hostHolder.getUser();

        if (user == null) {
            return CommunityUtil.getJSONString(1, "您尚未登录，请先登录！");
        }

        followService.followOrUnfollow(user.getId(), entityType, entityId);

        return CommunityUtil.getJSONString(0);
    }
}
