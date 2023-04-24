package com.nowcoder.community.dao;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community.dao
 * @Author: zhangguoliu
 * @CreateTime: 2023-04-24  09:59
 * @Description: TODO
 * @Version: 1.0
 */
@Mapper
public interface LoginTicketMapper {
    // 也可以 @Insert({"", "", ""}) 因为底层的 value 是一个 String[] 数组
    // @Options(useGeneratedKeys = true, keyProperty = "id") 自动生成主键并注入给 id
    @Insert(
            "insert into login_ticket(user_id, ticket, status, expired) " +
                    "value(#{userId},#{ticket}, #{status}, #{expired})"
    )
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    @Select(
            "select id, user_id, ticket, status, expired from login_ticket " +
                    "where ticket = #{ticket}"
    )
    LoginTicket selectByTicket(String ticket);

    // 演示通过注解来写动态 SQL
    /*@Update({
            "<script>",
            "update login_ticket set status = #{status} " +
                    "where ticket = #{ticket}",
            "<if test=\"ticket!=null\"> ",
            "and 1=1",
            "</if>",
            "</sctipt>"
    })*/
    @Update(
            "update login_ticket set status = #{status} " +
                    "where ticket = #{ticket}"
    )
    int updateStatus(String ticket, int status);
}
