package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private UserService userService;

    @Autowired
    private EventProducer eventProducer;

    @PostMapping("/followOrNot")
    @ResponseBody
    public String followOrUnfollow(int entityType, int entityId) {
        User user = hostHolder.getUser();

        if (user == null) {
            return CommunityUtil.getJSONString(1, "您尚未登录，请先登录！");
        }

        followService.followOrUnfollow(user.getId(), entityType, entityId);

        // kafka 触发关注事件
        boolean followStatus = followService.getFollowStatus(user.getId(), entityType, entityId);
        if (followStatus) {
            Event event = new Event()
                    .setTopic(TOPIC_FOLLOW)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityId);
            eventProducer.fireEvent(event);
        }

        return CommunityUtil.getJSONString(0);
    }

    @GetMapping("/followee/{userId}")
    public String getFolloweeList(@PathVariable("userId") int userId, Page page, Model model) {

        User u = userService.findUserById(userId);
        if (u == null) {
            throw new IllegalArgumentException("参数不正确！");
        }
        model.addAttribute("u", u);

        page.setLimit(5);
        page.setPath("/followee/" + userId);
        page.setRows(followService.getFolloweeCount(userId, ENTITY_TYPE_USER));

        List<Map<String, Object>> followeeList =
                followService.getFolloweeList(userId, ENTITY_TYPE_USER, page.getOffset(), page.getLimit());
        if (followeeList == null) {
            model.addAttribute("hasFollowee", false);
            return "/site/followee";
        }
        model.addAttribute("hasFollowee", true);

        List<Map<String, Object>> users = new ArrayList<>();
        for (Map<String, Object> followee : followeeList) {
            Map<String, Object> map = new HashMap<>();
            User user = userService.findUserById((int) followee.get("userId"));
            map.put("user", user);
            map.put("followTime", followee.get("followTime"));

            boolean followStatus = false;
            if (hostHolder.getUser() != null) {
                followStatus = followService.getFollowStatus(hostHolder.getUser().getId(), ENTITY_TYPE_USER, user.getId());
            }
            map.put("followStatus", followStatus);
            users.add(map);
        }
        model.addAttribute("users", users);

        return "/site/followee";
    }

    @GetMapping("/follower/{userId}")
    public String getFollowerList(@PathVariable("userId") int userId, Page page, Model model) {

        User u = userService.findUserById(userId);
        if (u == null) {
            throw new IllegalArgumentException("参数不正确！");
        }
        model.addAttribute("u", u);

        page.setLimit(5);
        page.setPath("/follower/" + userId);
        page.setRows(followService.getFollowerCount(ENTITY_TYPE_USER, userId));

        List<Map<String, Object>> followerList =
                followService.getFollowerList(ENTITY_TYPE_USER, userId, page.getOffset(), page.getLimit());
        if (followerList == null) {
            model.addAttribute("hasFollower", false);
            return "/site/follower";
        }
        model.addAttribute("hasFollower", true);

        List<Map<String, Object>> users = new ArrayList<>();
        for (Map<String, Object> follower : followerList) {
            Map<String, Object> map = new HashMap<>();
            User user = userService.findUserById((int) follower.get("userId"));
            map.put("user", user);
            map.put("followTime", follower.get("followTime"));

            boolean followStatus = false;
            if (hostHolder.getUser() != null) {
                followStatus = followService.getFollowStatus(hostHolder.getUser().getId(), ENTITY_TYPE_USER, user.getId());
            }
            map.put("followStatus", followStatus);
            users.add(map);
        }
        model.addAttribute("users", users);

        return "/site/follower";
    }
}
