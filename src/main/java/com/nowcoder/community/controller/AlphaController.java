package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import com.nowcoder.community.util.CommunityUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community.controller
 * @Author: zhangguoliu
 * @CreateTime: 2023-04-06  21:48
 * @Description: TODO
 * @Version: 1.0
 */
@Controller
@RequestMapping("/alpha")
public class AlphaController {
    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello() {
        return "Hello SpringBoot";
    }

    @Autowired  // AlphaController 依赖 AlphaService，所以注入 AlphaService，称依赖注入
    private AlphaService alphaService;

    @RequestMapping("/data")
    @ResponseBody
    public String find() {
        return alphaService.find();
    }

    // 处理和响应浏览器的请求（底层）
    // 通过方法参数直接获取 HttpServletRequest 和 HttpServletResponse 对象
    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) {
        // 获取 请求行、请求头、请求体
        System.out.println(request.getMethod());
        System.out.println(request.getContextPath());
        System.out.println(request.getServletPath());
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            System.out.println(headerName + ": " + headerValue);
        }
        String code = request.getParameter("code");
        System.out.println("code=" + code);

        // 返回响应数据
        response.setContentType("text/html;charset=utf-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.write("<h1>牛客网</h1>");
            writer.write("<h1>code=" + code + "</h1>");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 指定 GET 请求，传递参数方式一
    // 重点掌握 @RequestParam 注解的用法
    @RequestMapping(path = "/students", method = RequestMethod.GET)    // ServletPath 带参数
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "current", required = false, defaultValue = "1") Integer current,
            @RequestParam(name = "limit", required = false, defaultValue = "50") Integer limit
    ) {
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    // 指定 GET 请求，传递参数方式二
    // 重点掌握 @PathVariable 注解的用法
    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(
            @PathVariable(name = "id") Integer id
    ) {
        System.out.println(id);
        return "a student, id: " + id;
    }

    // 指定 POST 请求
    // 重点注意方法的参数要和表单的参数名称一致（可以不用 @RequestParam 等注解）
    // 填写表单路径：http://localhost:8080/community/html/student.html
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, Integer age) {
        System.out.println(name);
        System.out.println(age);
        return "保存成功";
    }

    // 响应 HTML
    // 方式一：使用 ModelAndView
    @GetMapping("/teacher")
    public ModelAndView getTeacher() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("name", "Tom");
        mav.addObject("age", 25);
        mav.setViewName("/view/demo");  // 不用 .html 的后缀
        return mav;
    }

    // 方式二：使用 Model
    @GetMapping("/school")
    public String getSchool(Model model) {  // DispatcherServlet 可以自动发现 Model 的引用
        model.addAttribute("name", "华中大");
        model.addAttribute("age", 70);
        return "/view/demo";    // 需要返回 view 的路径
    }

    // 响应 JSON （通常是针对异步请求）
    // 键值对形式的数据（Java 对象或 Map 对象）→ JSON 字符串 → JS 对象
    // 响应单个 JSON 数据
    @GetMapping("/emp")
    @ResponseBody   // 返回 JSON 数据需要加此注解
    // 响应头 Content-Type：application/json
    public Map<String, Object> getEmp() {
        Map<String, Object> map = new HashMap<>();
        map.put("姓名", "张三");
        map.put("年龄", 25);
        map.put("薪水", 8000);
        return map;
    }

    // 响应集合形式的 JSON 数据（数组、列表）
    @GetMapping("/emps")
    @ResponseBody
    public List<Map<String, Object>> getEmps() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("姓名", "张三");
        map.put("年龄", 25);
        map.put("薪水", 8000);
        list.add(map);
        map = new HashMap<>();
        map.put("姓名", "李四");
        map.put("年龄", 26);
        map.put("薪水", 9000);
        list.add(map);
        map = new HashMap<>();
        map.put("姓名", "王五");
        map.put("年龄", 27);
        map.put("薪水", 20000);
        list.add(map);
        return list;
    }

    // Cookie 示例
    @GetMapping("/cookie/set")
    @ResponseBody
    public String setCookie(HttpServletResponse response) {
        // 创建 Cookie
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());

        // 设置 Cookie 生效的路径范围
        cookie.setPath("/community/alpha");

        // 设置 Cookie 的生存时间（单位 秒）
        cookie.setMaxAge(60 * 10);

        // 发送 Cookie
        response.addCookie(cookie);
        return "set cookie";
    }

    @GetMapping("/cookie/get")
    @ResponseBody
    public String getCookie(@CookieValue("code") String code) {
        System.out.println(code);
        return "get cookie";
    }

    // Session 示例
    @GetMapping("/session/set")
    @ResponseBody
    public String setSession(HttpSession session) { // 此处 HttpSession 可以像 Model 一样注入
        session.setAttribute("id", 1);
        session.setAttribute("name", "Test");
        return "set session";
    }

    @GetMapping("/session/get")
    @ResponseBody
    public String getSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object id = session.getAttribute("id");
        Object name = session.getAttribute("name");
        System.out.println(id);
        System.out.println(name);
        return "get session";
    }
}
