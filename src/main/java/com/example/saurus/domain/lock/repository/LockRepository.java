package com.example.saurus.domain.lock.repository;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class LockRepository {

    private final StringRedisTemplate redisTemplate;

    public LockRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 락 획득
    public boolean acquireLock(String lockKey, String lockValue, long lockTimeout) {
        Boolean result = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, lockTimeout, TimeUnit.MILLISECONDS);
        return result != null && result;
    }

    // 락 해제
    public void releaseLock(String lockKey, String lockValue) {
        String currentValue = redisTemplate.opsForValue().get(lockKey);

        if (lockValue.equals(currentValue)) {
            redisTemplate.delete(lockKey);
        }
    }
}

