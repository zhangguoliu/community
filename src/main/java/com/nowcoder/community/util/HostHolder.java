package com.nowcoder.community.util;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community.util
 * @Author: zhangguoliu
 * @CreateTime: 2023-04-25  10:23
 * @Description: TODO 持有根据 ticket 查询到用户（user）信息，其实就是用于代替 Session 对象的，实现线程隔离
 * TODO（事实上 Session 的替代方案是数据库，而 ThreadLocal 只是打了一个辅助）
 * @Version: 1.0
 */
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    // 请求结束后，清理当前 ThreadLocal 中的 user，防止占用越来越多
    public void clear() {
        users.remove();
    }
}
