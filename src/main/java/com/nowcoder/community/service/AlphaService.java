package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

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
}
