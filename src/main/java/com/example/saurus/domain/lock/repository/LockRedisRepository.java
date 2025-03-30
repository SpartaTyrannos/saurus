package com.example.saurus.domain.lock.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class LockRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public LockRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Spin Lock을 획득하는 메서드 (setIfAbsent 사용)
    public boolean acquireLock(String lockKey, String lockValue, long expirationTimeMs) {
        long end = System.currentTimeMillis() + expirationTimeMs;

        while (System.currentTimeMillis() < end) {
            // setIfAbsent를 사용하여 락을 획득할 수 있으면 true, 실패하면 false 반환
            Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, expirationTimeMs, TimeUnit.MILLISECONDS);
            if (Boolean.TRUE.equals(success)) {
                return true;  // 락 획득 성공
            }

            try {
                // 락을 획득할 수 없으면 잠시 대기
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return false;  // 주어진 시간 내에 락을 획득하지 못하면 false 반환
    }

    // 락을 해제하는 메서드
    public boolean releaseLock(String lockKey, String lockValue) {
        String storedValue = redisTemplate.opsForValue().get(lockKey);
        if (lockValue.equals(storedValue)) {
            // 동일한 lockValue만 해제 가능
            return Boolean.TRUE.equals(redisTemplate.delete(lockKey));
        }
        return false;
    }

    // TTL을 연장하는 메서드
    public void extendLockTTL(String lockKey, long expirationTimeMs) {
        redisTemplate.expire(lockKey, expirationTimeMs, TimeUnit.MILLISECONDS);
    }
}

