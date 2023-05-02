package com.nowcoder.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.TimeUnit;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community
 * @Author: zhangguoliu
 * @CreateTime: 2023-05-02  11:35
 * @Description: TODO 测试 Redis 命令
 * @Version: 1.0
 */

@SpringBootTest
// 使用和 CommunityApplication 一样的配置类
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTests {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 测试简便写法：多次访问同一个 key 时绑定操作
    @Test
    public void testBoundOperations() {
        String redisKey = "testBound:value";

        // redisTemplate.boundXxx...
        BoundValueOperations<String, Object> ops = redisTemplate.boundValueOps(redisKey);

        // set
        ops.set("bound success");

        // get
        System.out.println(ops.get());
    }

    /* 测试事务管理（不完全满足 ACID 特性），机制：开启事务后，所有命令进入队列中，待提交事务后一并执行
     * 因此要特别注意：不要在 Redis 事务的中间做查询。并且多用编程式事务，目的是缩小事务的范围
     * 测试编程式事务
     */
    @Test
    public void testTransactional() {
        Object obj = redisTemplate.execute(new SessionCallback<>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // 开始事务
                operations.multi();

                // 命令入队
                BoundSetOperations ops = operations.boundSetOps("test:tx");
                ops.add("ok", "yes", "done");

                // 不要在一个事务中查询！
                System.out.println(ops.members());

                // 提交事务
                return operations.exec();

                // 回滚事务
                /*
                 * operations.discard();
                 * return null;
                 */
            }
        });
        System.out.println(obj);
    }

    // 测试通用命令
    @Test
    public void testGeneric() {
        redisTemplate.opsForList().leftPush("testGeneric", "exists");

        // keys
        System.out.println(redisTemplate.keys("*"));

        // del
        redisTemplate.opsForValue().set("testGeneric:del", "del");
        System.out.println(redisTemplate.delete("testGeneric:del"));

        // exists
        System.out.println(redisTemplate.hasKey("testGeneric"));

        // expire
        System.out.println(redisTemplate.expire("testGeneric", 10, TimeUnit.SECONDS));

        // ttl
        System.out.println(redisTemplate.getExpire("testGeneric", TimeUnit.SECONDS));
    }

    // 测试 string
    @Test
    public void testString() {
        String redisKey = "test:count";

        // set
        redisTemplate.opsForValue().set(redisKey, 1);

        // get
        System.out.println(redisTemplate.opsForValue().get(redisKey));

        // incr
        System.out.println(redisTemplate.opsForValue().increment(redisKey));

        // decr
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }

    // 测试 hash
    @Test
    public void testHash() {
        String redisKey = "test:user";

        // hset
        redisTemplate.opsForHash().put(redisKey, "id", 1);
        redisTemplate.opsForHash().put(redisKey, "username", "zhangsan");

        // hget
        System.out.println(redisTemplate.opsForHash().get(redisKey, "id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey, "username"));
    }

    // 测试 list
    @Test
    public void testList() {
        String redisKey = "test:ids";

        // lpush
        redisTemplate.opsForList().leftPush(redisKey, 101);
        redisTemplate.opsForList().leftPush(redisKey, 102);
        redisTemplate.opsForList().leftPush(redisKey, 103);

        // llen
        System.out.println(redisTemplate.opsForList().size(redisKey));

        // lindex
        System.out.println(redisTemplate.opsForList().index(redisKey, 0));

        // lrange
        System.out.println(redisTemplate.opsForList().range(redisKey, 0, 2));

        // rpop
        System.out.println(redisTemplate.opsForList().rightPop(redisKey));
        System.out.println(redisTemplate.opsForList().rightPop(redisKey));
        System.out.println(redisTemplate.opsForList().rightPop(redisKey));
    }

    // 测试 set
    @Test
    public void testSet() {
        String redisKey = "test:roles";

        // sadd
        redisTemplate.opsForSet().add(redisKey, "刘备", "关羽", "张飞", "赵云", "马超", "黄忠", "诸葛亮");

        // scard
        System.out.println(redisTemplate.opsForSet().size(redisKey));

        // srem
        System.out.println(redisTemplate.opsForSet().pop(redisKey));

        // smembers
        System.out.println(redisTemplate.opsForSet().members(redisKey));
    }

    // 测试 sorted_set
    @Test
    public void testZSet() {
        String redisKey = "test:students";

        // zadd
        redisTemplate.opsForZSet().add(redisKey, "Jack", 85);
        redisTemplate.opsForZSet().add(redisKey, "Lucy", 89);
        redisTemplate.opsForZSet().add(redisKey, "Rose", 82);
        redisTemplate.opsForZSet().add(redisKey, "Tom", 95);
        redisTemplate.opsForZSet().add(redisKey, "Jerry", 78);
        redisTemplate.opsForZSet().add(redisKey, "Amy", 92);
        redisTemplate.opsForZSet().add(redisKey, "Miles", 76);

        // zrem
        System.out.println(redisTemplate.opsForZSet().remove(redisKey, "Tom"));

        // zscore
        System.out.println(redisTemplate.opsForZSet().score(redisKey, "Amy"));

        // zrevrank
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey, "Rose"));

        // zcount
        System.out.println(redisTemplate.opsForZSet().count(redisKey, 0, 80));

        // zincrby
        System.out.println(redisTemplate.opsForZSet().incrementScore(redisKey, "Amy", 2));

        // zrevrange
        System.out.println(redisTemplate.opsForZSet().reverseRangeWithScores(redisKey, 0, 2));

        // zrangebyscore
        System.out.println(redisTemplate.opsForZSet().rangeByScoreWithScores(redisKey, 0, 80));
    }

    // 模拟阻塞队列
    @Test
    public void testBlockingQueue1() {
        BoundListOperations<String, Object> q1 = redisTemplate.boundListOps("q1");

        Object o = q1.rightPop(30, TimeUnit.SECONDS);
        System.out.println(o);
    }

    @Test
    public void testBlockingQueue2() {
        BoundListOperations<String, Object> q2 = redisTemplate.boundListOps("q2");
        System.out.println(q2.leftPush("ok"));
    }
}
