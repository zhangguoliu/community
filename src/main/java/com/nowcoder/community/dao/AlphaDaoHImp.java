package com.nowcoder.community.dao;

import org.springframework.stereotype.Repository;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community.dao
 * @Author: zhangguoliu
 * @CreateTime: 2023-04-07  21:01
 * @Description: TODO
 * @Version: 1.0
 */
@Repository("h")
public class AlphaDaoHImp implements AlphaDao {
    @Override
    public String select() {
        return "H";
    }
}
