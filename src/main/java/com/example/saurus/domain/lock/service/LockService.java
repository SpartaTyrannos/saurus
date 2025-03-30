package com.example.saurus.domain.lock.service;

import com.example.saurus.domain.lock.repository.LockRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

@Service
@RequiredArgsConstructor
public class LockService {
    private final LockRedisRepository lockRedisRepository;
    private final ThreadPoolTaskScheduler taskScheduler;  // TTL 연장을 위한 스케줄러

    private static final long LOCK_EXPIRATION_MS = 10000; // 락 만료 시간 (10초)
    private static final int RETRY_COUNT = 5;
    private static final long RETRY_DELAY_MS = 100; // 재시도 간격 (100ms)

    // 락을 획득하고 비즈니스 로직을 실행하는 메서드
    public <T> T executeWithLock(String lockKey, LockExecution<T> execution) {
        String lockValue = UUID.randomUUID().toString();  // 고유한 lock 값 생성
        ScheduledFuture<?> ttlExtender = null;

        try {
            // 락 획득 시도 (최대 RETRY_COUNT 횟수만큼 재시도)
            for (int i = 0; i < RETRY_COUNT; i++) {
                if (lockRedisRepository.acquireLock(lockKey, lockValue, LOCK_EXPIRATION_MS)) {
                    break;
                }
                // 재시도 대기
                Thread.sleep(RETRY_DELAY_MS);
            }

            // 락을 획득하지 못하면 예외 발생
            if (!lockRedisRepository.acquireLock(lockKey, lockValue, LOCK_EXPIRATION_MS)) {
                throw new RuntimeException("Lock 획득 실패: " + lockKey);
            }

            // 락 TTL 연장 스케줄러 실행
            ttlExtender = scheduleTTLRefresh(lockKey);

            // 비즈니스 로직 실행
            return execution.execute();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred");
        } finally {
            if (ttlExtender != null) {
                ttlExtender.cancel(true);  // TTL 연장 스케줄러 취소
            }
            lockRedisRepository.releaseLock(lockKey, lockValue);  // 락 해제
        }
    }

    private ScheduledFuture<?> scheduleTTLRefresh(String lockKey) {
        return taskScheduler.scheduleAtFixedRate(
                () -> lockRedisRepository.extendLockTTL(lockKey, LOCK_EXPIRATION_MS),  // Runnable
                LOCK_EXPIRATION_MS / 2  // 주기 (TTL의 절반)
        );
    }

    // 비즈니스 로직을 실행하기 위한 인터페이스
    @FunctionalInterface
    public interface LockExecution<T> {
        T execute();  // 락을 획득한 후 실행할 비즈니스 로직
    }
}



