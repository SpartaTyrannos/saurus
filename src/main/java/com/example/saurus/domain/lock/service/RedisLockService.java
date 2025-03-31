package com.example.saurus.domain.lock.service;

import com.example.saurus.domain.lock.repository.LockRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

@Service
public class RedisLockService {

    private final LockRedisRepository lockRedisRepository;


    private static final long LOCK_TIMEOUT_MS = 10000;
    private static final int MAX_RETRY = 3;
    private static final long RETRY_DELAY_MS = 100;

    public RedisLockService(LockRedisRepository lockRedisRepository) {
        this.lockRedisRepository = lockRedisRepository;
    }

    /**
     *
     * @param lockKey
     * @return
     */
    public String acquireLock(String lockKey) {
        String token = UUID.randomUUID().toString();
        int attempts = 0;

        while (attempts < MAX_RETRY) {
            attempts++;

            boolean success = lockRedisRepository.acquireLock(lockKey, token, LOCK_TIMEOUT_MS);
            if (success) {
                return token;
            }
            try {
                Thread.sleep(RETRY_DELAY_MS);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return null;
    }

    /**
     * @param lockKey
     * @param token
     */
    public void releaseLock(String lockKey, String token) {
        if (token != null) {
            lockRedisRepository.releaseLock(lockKey, token);
        }
    }
    /**
     * @param lockKey
     * @param action
     */
    public void executeWithLock(String lockKey, Runnable action) {

        String token = acquireLock(lockKey);
        if (token == null) {
            throw new RuntimeException("Failed to acquire lock for key: " + lockKey);
        }

        try {

            action.run();
        } finally {

            releaseLock(lockKey, token);
        }
    }
}


