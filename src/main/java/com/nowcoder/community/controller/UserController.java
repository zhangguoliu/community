package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.*;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community.controller
 * @Author: zhangguoliu
 * @CreateTime: 2023-04-25  14:33
 * @Description: TODO
 * @Version: 1.0
 */
@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private CommentService commentService;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @LoginRequired
    @GetMapping("/setting")
    public String getUserSettingPage() {
        return "/site/setting";
    }

    // 上传的文件通过 MultipartFile 管理
    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片！");
            return "/site/setting";
        }

        // 上传的文件不能按原始的文件名来存，防止文件名覆盖

        // 获取原始文件名
        String filename = headerImage.getOriginalFilename();

        // 截取原始文件名的后缀
        String suffix = filename.substring(filename.lastIndexOf("."));

        // 判断后缀是否为空
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件的格式不正确！");
            return "/site/setting";
        }

        // 生成随机文件名
        filename = CommunityUtil.generateUUID() + suffix;

        // 确定文件存放的路径
        File dest = new File(uploadPath + "/" + filename);

        // 将上传的文件存放到本地的文件中
        try {
            // 存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败！" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常！" + e);
        }

        // 更新当前用户的头像的路径（自定义 Web 访问路径）
        // 给用户提供读取图片的方法的时候，就需要按此路径来处理请求
        // http://localhost:8080/community/user/header/xxx.png
        String headerUrl = domain + contextPath + "/user/header/" + filename;

        // 当前请求持有用户
        User user = hostHolder.getUser();

        // 更新 headerUrl
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    // 获取头像
    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName,
                          HttpServletResponse response) {
        // 获取服务器存放路径
        fileName = uploadPath + "/" + fileName;

        // 获取文件的格式，通过输出文件的后缀（不要带 . ）
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);

        // 响应图片格式（是二进制的数据，需要用到字节流）
        response.setContentType("image/" + suffix);

        // 输入流自己创建需要手动关闭，输出流可以自动关闭
        try (FileInputStream fis = new FileInputStream(fileName)) {
            ServletOutputStream os = response.getOutputStream();
            byte[] buff = new byte[1024];
            int b = 0;
            while ((b = fis.read(buff)) != -1) {
                os.write(buff, 0, b);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 修改密码
    @LoginRequired
    @PostMapping("/updatePassword")
    public String updatePassword(String oldPassword, String newPassword, Model model) {
        User user = hostHolder.getUser();
        Map<String, Object> map = userService.updatePassword(user.getId(), oldPassword, newPassword);

        if (map == null || map.isEmpty()) {
            return "redirect:/logout";
        } else {
            model.addAttribute("oldPasswordMsg", map.get("oldPasswordMsg"));
            model.addAttribute("newPasswordMsg", map.get("newPasswordMsg"));
            return "/site/setting";
        }
    }

    // 个人主页
    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {

        User user = userService.findUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("该用户不存在！");
        }
        model.addAttribute("user", user);

        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);

        // 如果是当前用户自己的个人主页，则没必要获取对自己的关注状态
        boolean followStatus = false;
        if (hostHolder.getUser() != null && hostHolder.getUser().getId() != userId) {
            followStatus = followService.getFollowStatus(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("followStatus", followStatus);

        // 获取当前主页用户的关注的用户的数量
        long followeeCount = followService.getFolloweeEntityCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);

        // 获取当前主页用户的粉丝的数量
        long followerCount = followService.getFollowerEntityCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);

        return "site/profile";
    }

    @GetMapping("/post/my/{userId}")
    public String getUserPostsPage(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("参数不正确！");
        }
        model.addAttribute("user", user);

        int rows = discussPostService.findDiscussPostRows(userId);
        model.addAttribute("rows", rows);

        page.setLimit(5);
        page.setPath("/user/post/my/" + userId);
        page.setRows(rows);

        List<DiscussPost> discussPosts = discussPostService.findDiscussPosts(userId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> posts = new ArrayList<>();
        for (DiscussPost post : discussPosts) {
            Map<String, Object> map = new HashMap<>();
            map.put("post", post);
            long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
            map.put("likeCount", likeCount);
            posts.add(map);
        }
        model.addAttribute("posts", posts);
        return "/site/my-post";
    }

    @GetMapping("/comment/{userId}")
    public String getUserCommentsPage(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("参数不正确！");
        }
        model.addAttribute("user", user);

        int commentCount = commentService.findCommentCountByUserId(userId);
        model.addAttribute("commentCount", commentCount);

        page.setLimit(5);
        page.setPath("/user/comment/" + userId);
        page.setRows(commentCount);

        List<Comment> list = commentService.findCommentsByUserId(userId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> comments = new ArrayList<>();
        for (Comment comment : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("comment", comment);

            // 此回复对应的帖子
            DiscussPost post = null;
            if (comment.getEntityType() == ENTITY_TYPE_POST) {
                post = discussPostService.findDiscussPostById(comment.getEntityId());
            } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
                int commentForPostId = comment.getEntityId();
                int postId = commentService.getPostByCommentId(commentForPostId);
                post = discussPostService.findDiscussPostById(postId);
            }
            map.put("post", post);
            comments.add(map);
        }
        model.addAttribute("comments", comments);
        return "/site/my-reply";
    }
}
