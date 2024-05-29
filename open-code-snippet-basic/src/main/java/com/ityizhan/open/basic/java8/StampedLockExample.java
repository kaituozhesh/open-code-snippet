package com.ityizhan.open.basic.java8;

import java.util.concurrent.*;
import java.util.concurrent.locks.StampedLock;

/**
 * @Description: StampedLock 锁测试
 * @ClassName: StampedLockExample
 * @Auther: lin
 * @Date: 2024/5/29 15:43
 * @Version: 1.0
 */
public class StampedLockExample {

    public static void main(String[] args) throws InterruptedException {

        int threadCount = 1000;
        CountDownLatch countDownLatch = new CountDownLatch(threadCount * 2);
        ExecutorService executorService = Executors.newCachedThreadPool();

        Counter counter = new Counter();
        for (int i = 0; i < threadCount; i++) {
            // 写入线程
            executorService.execute(() -> {
                counter.increment();
                countDownLatch.countDown();
            });
            // 乐观读
            executorService.execute(() -> {
                counter.optimisticRead();
                countDownLatch.countDown();
            });
            // 悲观读
            executorService.execute(() -> {
                counter.read();
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        System.out.println("Final counter value: " + counter.read());
    }

}

class Counter {
    private int count;
    private final StampedLock lock = new StampedLock();

    public void increment() {
        long stamp = lock.writeLock();
        try {
            count++;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public int read() {
        long stamp = lock.readLock();
        try {
            return count;
        } finally {
            lock.unlockRead(stamp);
        }
    }

    public int optimisticRead() {
        long stamp = lock.tryOptimisticRead();
        int currentCount = count;

        // 检查在读取过程中是否有写操作
        if (!lock.validate(stamp)) {
            // 如果写锁已被获取，则升级为悲观读锁
            stamp = lock.readLock();
            try {
                currentCount = count;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return currentCount;
    }
}
