package com.nowcoder.community;

import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community
 * @Author: zhangguoliu
 * @CreateTime: 2023-04-14  16:59
 * @Description: TODO
 * @Version: 1.0
 */
@SpringBootTest
// 使用和 CommunityApplication 一样的配置类
@ContextConfiguration(classes = CommunityApplication.class)
public class UserMapperTests {
    /* Bug 解决记录：
    org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name
    解决方法，将 mybatis 依赖版本更换为 3.0.1
    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
        <version>3.0.1</version>
    </dependency>
    */

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSelect() {
        User user = userMapper.selectById(101);
        System.out.println(user);
        User liubei = userMapper.selectByName("liubei");
        System.out.println(liubei);
        User u = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(u);
    }

    @Test
    public void testInsert() {
        User user = new User();
        user.setUsername("Test");
        user.setPassword("123");
        user.setEmail("test@test.com");

        Integer i = userMapper.insertUser(user);
        System.out.println(i);
        Integer id = user.getId();
        System.out.println(id);

        User user1 = userMapper.selectById(id);
        System.out.println(user1);
    }

    @Test
    public void testUpdate() {
        Integer hello = userMapper.updatePassword(150, "hello");
        System.out.println(hello);

        Integer integer = userMapper.updateStatus(150, 1);
        System.out.println(integer);

        Integer integer1 = userMapper.updateHeader(150, "http://images.nowcoder.com/head/149t.png");
        System.out.println(integer1);
    }
}
