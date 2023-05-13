package com.nowcoder.community.entity;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community.entity
 * @Author: zhangguoliu
 * @CreateTime: 2023-04-15  17:57
 * @Description: TODO 封装分页相关的信息（注意在 setter 的时候需要判断数据的合法性）。面向对象的思维
 * @Version: 1.0
 */
public class Page {
    // 当前页码
    private int current = 1;

    // 一页显示帖子数量的上限
    private int limit = 10;

    // 帖子总数（用于计算总页数）
    private int rows;

    // 查询路径（复用分页的链接）
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current >= 1) {
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        // limit 过大会增加服务器和浏览器的压力
        if (limit >= 1) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @description: 获取当前页面的起始帖子在数据库中的起始行数
     * @author: zhangguoliu
     * @date: 2023/4/15 18:09
     * @return: int
     **/
    public int getOffset() {
        return (current - 1) * limit;
    }

    /**
     * @description: 获取总页数
     * @author: zhangguoliu
     * @date: 2023/4/15 18:13
     * @return: int
     **/
    public int getTotal() {
        int total = rows / limit;
        if (rows % limit != 0) {
            total++;
        }
        return total;
    }

    /**
     * @description: 获取起始页码
     * @author: zhangguoliu
     * @date: 2023/4/15 18:19
     * @return: int
     **/
    public int getFrom() {
        int from = current - 2;
        return Math.max(from, 1);
    }

    /**
     * @description: 获取结束页码
     * @author: zhangguoliu
     * @date: 2023/4/15 18:20
     * @return: int
     **/
    public int getTo() {
        int to = current + 2;
        int total = getTotal();
        return Math.min(to, total);
    }
}
