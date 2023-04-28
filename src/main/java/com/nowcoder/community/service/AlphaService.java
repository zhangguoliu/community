package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community.service
 * @Author: zhangguoliu
 * @CreateTime: 2023-04-13  17:50
 * @Description: TODO
 * @Version: 1.0
 */
@Service
@Scope("prototype") // 非单例模式（Spring 容器中各对象默认是单例模式的）
public class AlphaService {
    @Autowired  // AlphaService 依赖 AlphaDao，所以注入 AlphaDao，称依赖注入
    private AlphaDao alphaDao;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    public String find() {
        return alphaDao.select();
    }

    public AlphaService() {
        System.out.println("构造Bean...");
    }

    @PostConstruct  // Spring 管理 Bean 的初始化方法
    public void init() {
        System.out.println("初始化Bean...");
    }

    @PreDestroy // Spring 管理 Bean 的销毁方法
    public void destroy() {
        System.out.println("销毁Bean...");
    }

    // Spring 的事务管理示例：声明式事务（通过注解）
    // 事务的传播机制，常用的 3 个
    // REQUIRED：支持当前事务（外部事务；调用者的事务），如果外部事务不存在则创建新事务
    // REQUIRES_NEW：创建一个新事务，并且暂停当前事务（外部事务）
    // NESTED：如果当前存在事务（外部事务），则嵌套在该事务中执行，且有独立的提交和回滚，若外部事务不存在则同 REQUIRED
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save1() {

        // 新增用户
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 新增帖子
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("Hello");
        post.setContent("新人报道！");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

        // 人为制造错误（测试是否会回滚）
        Integer i = Integer.valueOf("abc");

        return "ok";
    }

    // Spring 的事务管理示例：编程式事务（通过 TransactionTemplate）
    public Object save2() {
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {

                // 新增用户
                User user = new User();
                user.setUsername("beta");
                user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
                user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
                user.setEmail("beta@qq.com");
                user.setHeaderUrl("http://image.nowcoder.com/head/999t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);

                // 新增帖子
                DiscussPost post = new DiscussPost();
                post.setUserId(user.getId());
                post.setTitle("你好");
                post.setContent("我是新人！");
                post.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(post);

                // 人为制造错误（测试是否会回滚）
                Integer i = Integer.valueOf("abc");

                return "ok";
            }
        });
    }
}
