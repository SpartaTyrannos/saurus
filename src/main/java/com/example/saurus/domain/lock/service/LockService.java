package com.example.saurus.domain.lock.service;

import com.example.saurus.domain.common.exception.CustomException;
import com.example.saurus.domain.lock.repository.LockRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
public class LockService {

    private final LockRepository lockRepository;
    private final int maxRetries = 5;  // 최대 재시도 횟수
    private final long lockTimeout = 5000L;  // 락 만료 시간 (5초)
    private final long retryInterval = 1000L;  // 재시도 간격 (1초)

    public LockService(LockRepository lockRepository) {
        this.lockRepository = lockRepository;
    }

    // 락을 획득하고 처리하는 메서드
    public <T> T executeWithLock(String lockKey, LockCallback<T> callback) {
        String lockValue = UUID.randomUUID().toString();
        int attempt = 0;

        while (attempt < maxRetries) {
            // 락을 획득 시도
            if (lockRepository.acquireLock(lockKey, lockValue, lockTimeout)) {
                try {
                    return callback.execute();  // 락을 획득했으므로 실제 작업을 실행
                } finally {
                    // 작업 완료 후 락 해제
                    lockRepository.releaseLock(lockKey, lockValue);
                }
            } else {
                attempt++;
                if (attempt >= maxRetries) {
                    throw new CustomException(HttpStatus.SERVICE_UNAVAILABLE, "다른 요청이 처리 중입니다. 잠시 후 다시 시도해주세요.");
                }
                // 재시도 대기
                try {
                    Thread.sleep(retryInterval);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "재시도 대기 중 오류 발생.");
                }
            }
        }

        // 최대 재시도 횟수를 초과하면 실패
        throw new CustomException(HttpStatus.SERVICE_UNAVAILABLE, "다시 시도해 주세요.");
    }

    // 락을 획득하여 실행할 콜백 함수
    public interface LockCallback<T> {
        T execute();
    }
}




