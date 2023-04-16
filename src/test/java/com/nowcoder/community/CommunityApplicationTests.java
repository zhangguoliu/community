package com.nowcoder.community;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.service.AlphaService;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/* Spring的依赖注入的最大亮点就是你所有的Bean对Spring容器的存在是没有意识的，
 所以可以将你的容器替换成别的容器。但是在实际的项目中，
 我们不可避免的要用到Spring容器本身的功能资源，这时候Bean必须要意识到Spring容器的存在，
 才能调用Spring所提供的资源，这就是所谓的Spring Aware。
 其实Spring Aware本来就是Spring设计用来框架内部使用的，若使用了Spring Aware，
 你的Bean将会和Spring框架耦合。Spring Aware的目的就是为了让Bean获得Spring容器的服务。*/
@SpringBootTest
// 使用和 CommunityApplication 一样的配置类
@ContextConfiguration(classes = CommunityApplication.class)
class CommunityApplicationTests implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    /*ApplicationContext 即为 Spring中的容器
     *ApplicationContext extends HierarchicalBeanFactory extends BeanFactory*/
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /*通过 ApplicationContextAware 获取 Spring中的容器 ApplicationContext，
     *通过 ApplicationContext 获取容器中的各种 bean 组件，注册监听事件，加载资源文件等功能。*/
    @Test
    public void testApplicationContext() {
        // 容器实现类
        // org.springframework.web.context.support.GenericWebApplicationContext@5b408dc3
        System.out.println(applicationContext);

        // 通过（接口）类型获取
        // 接口的两个实现类中可以通过注解 @Primary 优先获取
        AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
        System.out.println(alphaDao);

        // 通过 Bean 的默认名获取 Bean
        AlphaDao alphaDaoMImp = applicationContext.getBean("alphaDaoMImp", AlphaDao.class);
        System.out.println(alphaDaoMImp.select());

        // 通过 Bean 的自定义别名获取 Bean
        AlphaDao h = applicationContext.getBean("h", AlphaDao.class);
        System.out.println(h.select());
    }

    // 测试 Spring 对 Bean 的管理（自定义的类）
    // 管理初始化方法、销毁方法，管理单例模式、非单例模式
    // @Scope("prototype") 注解表示非单例模式（Spring 容器中各对象默认是单例模式的）
    @Test
    public void testBeanManagement() {
        AlphaService alphaService = applicationContext.getBean(AlphaService.class);
        System.out.println(alphaService);

        alphaService = applicationContext.getBean(AlphaService.class);
        System.out.println(alphaService);
    }

    // 测试 Spring 容器获取和管理外部 jar 包中的 Bean
    // 通过配置类上注解 @Configuration
    // 配置类的获取 Bean 方法上注解 @Bean
    /*@Test
    public void testBeanConfig() {
        AlphaConfig alphaConfig = applicationContext.getBean(AlphaConfig.class);
        System.out.println(alphaConfig.simpleDateFormat().format(new Date()));
    }*/

    // 下面测试获取 Bean 的更简便的方法
    // 通过依赖注入（DI）
    // @Autowired 可以修饰成员变量（最简单）、Setter 方法和构造方法

    @Autowired
    @Qualifier("h") // 指定 Bean 的别名
    private AlphaDao alphaDao;

    @Autowired
    private AlphaService alphaService;

    // 使用非自定义 Bean 仍然需要有 @Configuration 注解的配置类和 @Bean 注解的方法
    @Autowired
    private SimpleDateFormat simpleDateFormat;

    // 测试依赖注入（DI）
    @Test
    public void testDI() {
        System.out.println(alphaDao);
        System.out.println(alphaService);
        System.out.println(simpleDateFormat);
        System.out.println(simpleDateFormat.format(new Date()));
    }

    @Autowired
    private HikariDataSource hikariDataSource;

    @Test
    public void testDB() {
        System.out.println("hikariDataSource: " + hikariDataSource);
        try {
            System.out.println(hikariDataSource.getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
