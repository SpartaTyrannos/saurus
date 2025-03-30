package com.example.saurus.domain.lock.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class LockRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public Boolean lock(String key) {
        return redisTemplate.opsForValue().setIfAbsent(key, "lock", Duration.ofSeconds(3));
    }

    public Boolean unlock(String key) {
        return redisTemplate.delete(key);
    }
}
