package com.example.demo.service;



import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Service
public class DistributedLockService {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 执行带锁的业务逻辑
     */
    public <T> T executeWithLock(String lockKey, long waitTime, long leaseTime, Supplier<T> business) {
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean acquired = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);

            if (!acquired) {
                log.warn("获取分布式锁失败，lockKey: {}", lockKey);
                throw new RuntimeException("系统繁忙，请稍后重试");
            }

            log.info("获取分布式锁成功，lockKey: {}", lockKey);
            return business.get();

        } catch (InterruptedException e) {
            log.error("获取分布式锁被中断，lockKey: {}", lockKey, e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("系统繁忙，请稍后重试");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("释放分布式锁成功，lockKey: {}", lockKey);
            }
        }
    }

    /**
     * 便捷方法：默认等待3秒，持有10秒
     */
    public <T> T executeWithLock(String lockKey, Supplier<T> business) {
        return executeWithLock(lockKey, 3, 10, business);
    }
}