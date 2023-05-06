package com.nowcoder.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community
 * @Author: zhangguoliu
 * @CreateTime: 2023-05-06  21:57
 * @Description: TODO
 * @Version: 1.0
 */

@SpringBootTest
// 使用和 CommunityApplication 一样的配置类
@ContextConfiguration(classes = CommunityApplication.class)
public class KafkaTests {
    @Autowired
    private KafkaProducer kafkaProducer;

    @Test
    public void TestKafka() {
        kafkaProducer.send("test", "ok");
        kafkaProducer.send("test", "中！");
        kafkaProducer.send("test", "see you again");
        kafkaProducer.send("test", "愿世界和平");
        try {
            Thread.sleep(1000 * 10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

@Component
class KafkaProducer {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void send(String topic, String content) {
        kafkaTemplate.send(topic, content);
    }
}

@Component
class KafkaConsumer {
    @KafkaListener(topics = {"test"})
    public void take(ConsumerRecord<String, Object> record) {
        System.out.println(record.value());
    }
}