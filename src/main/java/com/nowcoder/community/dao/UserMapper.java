package com.nowcoder.community.dao;

import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community.dao
 * @Author: zhangguoliu
 * @CreateTime: 2023-04-14  16:36
 * @Description: TODO
 * @Version: 1.0
 */
@Mapper // 效果同 @Repository
public interface UserMapper {
    User selectById(int id);

    User selectByName(String name);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateStatus(@Param("id") int id, @Param("status") int status);

    int updatePassword(@Param("id") int id, @Param("password") String password);

    int updateHeader(@Param("id") int id, @Param("headerUrl") String headerUrl);
}
