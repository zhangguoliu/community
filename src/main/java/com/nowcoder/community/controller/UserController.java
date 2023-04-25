package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
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
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @GetMapping("/setting")
    public String getUserSettingPage() {
        return "/site/setting";
    }

    // 上传的文件通过 MultipartFile 管理
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
}
