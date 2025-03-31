package com.example.saurus.domain.lock.repository;

import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Repository
public class LockRedisRepository {

    private final StringRedisTemplate redisTemplate;


    private static final String UNLOCK_LUA_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "   return redis.call('del', KEYS[1]) " +
                    "else " +
                    "   return 0 " +
                    "end";
    private final RedisScript<Long> unlockScript;

    public LockRedisRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.unlockScript = new DefaultRedisScript<>(UNLOCK_LUA_SCRIPT, Long.class);
    }

    /**
     * @param lockKey
     * @param value
     * @param expireMillis
     */
    public boolean acquireLock(String lockKey, String value, long expireMillis) {

        Boolean success = redisTemplate.execute((RedisCallback<Boolean>) connection ->
                connection.set(lockKey.getBytes(), value.getBytes(),
                        Expiration.milliseconds(expireMillis),
                        RedisStringCommands.SetOption.SET_IF_ABSENT)
        );
        return Boolean.TRUE.equals(success);
    }

    /**
     * @param lockKey
     * @param value
     */
    public boolean releaseLock(String lockKey, String value) {

        Long result = redisTemplate.execute(unlockScript, Collections.singletonList(lockKey), value);

        return result != null && result > 0;
    }
}

