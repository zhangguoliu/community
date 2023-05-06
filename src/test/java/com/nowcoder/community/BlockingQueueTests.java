package com.nowcoder.community;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @BelongsProject: community
 * @BelongsPackage: com.nowcoder.community
 * @Author: zhangguoliu
 * @CreateTime: 2023-05-06  15:08
 * @Description: TODO
 * @Version: 1.0
 */
public class BlockingQueueTests {
    public static void main(String[] args) {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);
        new Thread(new Producer(queue), "producer").start();
        new Thread(new Consumer(queue), "consumer1").start();
        new Thread(new Consumer(queue), "consumer2").start();
        new Thread(new Consumer(queue), "consumer3").start();
        new Thread(new Consumer(queue), "consumer4").start();
    }
}

class Producer implements Runnable {
    private BlockingQueue<Integer> queue;

    public Producer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            try {
                Thread.sleep(20);
                queue.put(i);
                System.out.println(Thread.currentThread().getName() + "生成：" + queue.size());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

class Consumer implements Runnable {
    private BlockingQueue<Integer> queue;

    public Consumer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(new Random().nextInt(1000));
                queue.take();
                System.out.println(Thread.currentThread().getName() + "消费：" + queue.size());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
