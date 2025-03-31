package com.example.saurus.domain.lock.service;

import com.example.saurus.domain.common.exception.CustomException;
import com.example.saurus.domain.lock.repository.LockRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
public class LockService {

    private final LockRedisRepository lockRedisRepository;
    private final TransactionTemplate transactionTemplate;

    public void executeWithLock(String key, Runnable action) {
        int retryCount = 10;
        int delay = 100;

        for (int i = 0; i < retryCount; i++) {
            // 락을 획득
            if (Boolean.TRUE.equals(lockRedisRepository.lock(key))) {
                try {
                    // 트랜잭션 내에서 작업을 처리
                    transactionTemplate.executeWithoutResult(status -> {
                        try {
                            // 트랜잭션 내에서 액션 실행
                            action.run();
                        } catch (Exception e) {
                            status.setRollbackOnly(); // 예외 발생 시 롤백
                            throw e;
                        }
                    });
                    return; // 성공적으로 트랜잭션이 끝났으면 종료
                } finally {
                    // 트랜잭션 작업이 끝난 후 락을 해제
                    lockRedisRepository.unlock(key);
                }
            }

            try {
                // 락 획득 실패 시 지연 후 재시도
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Thread interrupted");
            }
        }

        throw new CustomException(HttpStatus.CONFLICT, "잠금 획득 실패. 나중에 다시 시도해주세요.");
    }
}



