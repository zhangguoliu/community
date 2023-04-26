package com.nowcoder.community.controller;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community.controller
 * @Author: zhangguoliu
 * @CreateTime: 2023-04-15  15:10
 * @Description: TODO
 * @Version: 1.0
 */
@Controller
public class HomeController {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * @description: 显示帖子的时候，不会显示 userId，而是显示 userId 对应的姓名头像等
     * 解决的方法有两个 一是通过 SQL 语句联接查询。二是通过业务逻辑处理
     * @author: zhangguoliu
     * @date: 2023/4/15 15:21
     * @param: model
     * @return: java.lang.String
     **/
    @GetMapping("/index")
    // 知识点：SpringMVC 会自动将请求中的参数映射到 Controller 的方法的参数上，默认按照同名规则映射
    // 无论是基本类型与之同名，还是对象中的成员与之同名
    public String getIndexPage(Model model, Page page) {
        // 知识点：方法调用之前，Spring MVC 会自动实例化 Model 和 Page，并将 Page 注入 Model
        // 所以，在 Thymeleaf 可以直接访问 Page 对象中的数据

        /*问题：这里的 Page 为什么不需要加入类似的 @Component 注解使之被自动扫描呢？
        Page 只是一个实体，而且是多例的，每次请求都有不同的数据，需要重置 Page
        通常，封装逻辑的组件才需要由容器管理，为了便于复用这个逻辑。*/

        page.setRows(discussPostMapper.selectDiscussPostRows(0));
        page.setPath("/index"); // 路径复用

        List<DiscussPost> list = discussPostMapper.selectDiscussPosts
                (0, page.getOffset(), page.getLimit());
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                User user = userMapper.selectById(post.getUserId());
                map.put("user", user);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussposts", discussPosts);
        return "/index";
    }
}
