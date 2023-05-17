package com.nowcoder.community.controller;

import com.alibaba.fastjson2.JSONObject;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.MessageService;
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
import org.springframework.web.util.HtmlUtils;

import java.util.*;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community.controller
 * @Author: zhangguoliu
 * @CreateTime: 2023-04-29  19:12
 * @Description: TODO
 * @Version: 1.0
 */
@Controller
public class MessageController implements CommunityConstant {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    // 私信列表
    @GetMapping("/letter/list")
    public String getLetterList(Model model, Page page) {

        // 报错的情况，将来统一处理（3.31 统一处理异常）

        // 手动制造错误来测试
        // Integer.parseInt("abc");

        User user = hostHolder.getUser();

        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));

        // 会话列表
        List<Message> conversationList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationList != null) {
            for (Message message : conversationList) {
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message);
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                map.put("unreadCount",
                        messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target", userService.findUserById(targetId));

                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);

        // 查询未读消息总数
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(hostHolder.getUser().getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "/site/letter";
    }

    @GetMapping("/letter/detail/{conversationId}")
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model) {

        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        // 私信列表
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if (letterList != null) {

            // 当前分页读取的各私信是按时间倒序的，所以从后往前读：
            // 使得页面像微信一样，上面显示早先的私信，下面显示晚些的私信
            for (int i = letterList.size() - 1; i >= 0; i--) {
                Message message = letterList.get(i);
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                User fromUser = userService.findUserById(message.getFromId());
                map.put("fromUser", fromUser);

                // 修改：此私信的发出者是否为当前登录用户
                boolean isUserHere = message.getFromId() == hostHolder.getUser().getId();
                map.put("isUserHere", isUserHere);

                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);

        // 私信的目标
        model.addAttribute("target", getLetterTarget(conversationId));

        // 设置已读
        List<Integer> ids = getUnreadLetterIds(letterList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";
    }

    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        if (hostHolder.getUser().getId() == id0) {
            return userService.findUserById(id1);
        } else {
            return userService.findUserById(id0);
        }
    }

    private List<Integer> getUnreadLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();

        if (letterList != null) {
            for (Message message : letterList) {

                // 如果当前用户是接收者，并且该消息未读
                if (hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }

        return ids;
    }

    @PostMapping("/letter/send")
    @ResponseBody
    public String sendLetter(String toName, String content) {

        // 报错的情况，将来统一处理（3.31 统一处理异常）

        // 手动制造错误来测试
        // Integer.parseInt("abc");

        User target = userService.findUserByName(toName);
        if (target == null) {
            return CommunityUtil.getJSONString(1, "目标用户不存在");
        }

        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);

        return CommunityUtil.getJSONString(0);
    }

    @PostMapping("/letter/delete")
    @ResponseBody
    public String deleteLetter(int id) {
        List<Integer> ids = new ArrayList<>();
        ids.add(id);
        messageService.deleteMessage(ids);
        return CommunityUtil.getJSONString(0);
    }

    @GetMapping("/notice/list")
    public String getNoticeList(Model model) {

        Message latestNotice = messageService.findLatestMessage(hostHolder.getUser().getId(), TOPIC_COMMENT);
        if (latestNotice != null) {
            Map<String, Object> commentVo = new HashMap<>();
            commentVo.put("latestNotice", latestNotice);

            String content = latestNotice.getContent();
            String latestContent = HtmlUtils.htmlUnescape(content);
            HashMap<String, Object> map = JSONObject.parseObject(latestContent, HashMap.class);
            User user = userService.findUserById((Integer) map.get("userId"));
            commentVo.put("user", user);
            commentVo.put("entityType", map.get("entityTye"));
            commentVo.put("entityId", map.get("entityId"));
            commentVo.put("postId", map.get("postId"));

            int count = messageService.findNoticeCount(hostHolder.getUser().getId(), TOPIC_COMMENT);
            commentVo.put("count", count);

            int unreadCount = messageService.findNoticeUnreadCount(hostHolder.getUser().getId(), TOPIC_COMMENT);
            commentVo.put("unreadCount", unreadCount);
            model.addAttribute("commentVo", commentVo);
        }

        latestNotice = messageService.findLatestMessage(hostHolder.getUser().getId(), TOPIC_LIKE);
        if (latestNotice != null) {
            Map<String, Object> likeVo = new HashMap<>();
            likeVo.put("latestNotice", latestNotice);

            String content = latestNotice.getContent();
            String latestContent = HtmlUtils.htmlUnescape(content);
            HashMap<String, Object> map = JSONObject.parseObject(latestContent, HashMap.class);
            User user = userService.findUserById((Integer) map.get("userId"));
            likeVo.put("user", user);
            likeVo.put("entityType", map.get("entityTye"));
            likeVo.put("entityId", map.get("entityId"));
            likeVo.put("postId", map.get("postId"));

            int count = messageService.findNoticeCount(hostHolder.getUser().getId(), TOPIC_LIKE);
            likeVo.put("count", count);

            int unreadCount = messageService.findNoticeUnreadCount(hostHolder.getUser().getId(), TOPIC_LIKE);
            likeVo.put("unreadCount", unreadCount);
            model.addAttribute("likeVo", likeVo);
        }

        latestNotice = messageService.findLatestMessage(hostHolder.getUser().getId(), TOPIC_FOLLOW);
        if (latestNotice != null) {
            Map<String, Object> followVo = new HashMap<>();
            followVo.put("latestNotice", latestNotice);

            String content = latestNotice.getContent();
            String latestContent = HtmlUtils.htmlUnescape(content);
            HashMap<String, Object> map = JSONObject.parseObject(latestContent, HashMap.class);
            User user = userService.findUserById((Integer) map.get("userId"));
            followVo.put("user", user);
            followVo.put("entityType", map.get("entityTye"));
            followVo.put("entityId", map.get("entityId"));
            followVo.put("postId", map.get("postId"));

            int count = messageService.findNoticeCount(hostHolder.getUser().getId(), TOPIC_FOLLOW);
            followVo.put("count", count);

            int unreadCount = messageService.findNoticeUnreadCount(hostHolder.getUser().getId(), TOPIC_FOLLOW);
            followVo.put("unreadCount", unreadCount);
            model.addAttribute("followVo", followVo);
        }

        // 查询未读消息总数
        int letterUnreadCount = messageService.findLetterUnreadCount(hostHolder.getUser().getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        int noticeUnreadCount = messageService.findNoticeUnreadCount(hostHolder.getUser().getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "/site/notice";
    }

    @GetMapping("/notice/detail/{topic}")
    public String getNoticeDetail(@PathVariable("topic") String topic, Page page, Model model) {
        if (!("comment".equals(topic) || "like".equals(topic) || "follow".equals(topic))) {
            throw new IllegalArgumentException("参数不正确！");
        }

        page.setLimit(5);
        page.setRows(messageService.findNoticeCount(hostHolder.getUser().getId(), topic));
        page.setPath("/notice/detail/" + topic);

        List<Message> notices = messageService.findNotices(hostHolder.getUser().getId(), topic, page.getOffset(), page.getLimit());
        if (notices != null) {

            List<Map<String, Object>> noticeVos = new ArrayList<>();

            for (int i = notices.size() - 1; i >= 0; i--) {
                Message notice = notices.get(i);
                Map<String, Object> map = new HashMap<>();

                map.put("notice", notice);
                int fromId = notice.getFromId();
                User fromUser = userService.findUserById(fromId);
                map.put("fromUser", fromUser);

                String content = notice.getContent();
                content = HtmlUtils.htmlUnescape(content);
                HashMap<String, Object> hashMap = JSONObject.parseObject(content, HashMap.class);

                User user = userService.findUserById((Integer) hashMap.get("userId"));
                map.put("user", user);
                map.put("entityType", hashMap.get("entityType"));
                map.put("entityId", hashMap.get("entityId"));
                map.put("postId", hashMap.get("postId"));
                map.put("topic", topic);

                noticeVos.add(map);
            }
            model.addAttribute("noticeVos", noticeVos);

            // 设置已读
            List<Integer> ids = getUnreadLetterIds(notices);
            if (ids.size() > 0) {
                messageService.readMessage(ids);
            }
        }

        return "/site/notice-detail";
    }
}
