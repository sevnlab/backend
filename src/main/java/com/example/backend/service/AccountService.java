package com.example.backend.service;


import com.example.backend.dto.UseRequest;
import com.example.backend.entity.AccountInfo;
import com.example.backend.repository.AccountRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import jakarta.persistence.OptimisticLockException;


/**
 * 낙관적 + 비관적 통합 서비스
 *
 * 계좌 금액 차감 + 이력 저장
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    private final EntityManager entityManager;

    /**
     * ======================================================
     * ② 비관적 락 (Pessimistic Lock)
     * ------------------------------------------------------
     * - SELECT FOR UPDATE 로 DB가 직접 row-level lock 수행
     * - 트랜잭션 커밋 시 락 해제
     * - 동시에 접근하면 다른 세션은 대기
     * ======================================================
     */
    @Transactional
    public void useAmountPessimistic(UseRequest request) {
        String accountKey = request.getAccountKey();
        long useAmt = request.getAmount();

        // 1. 락 걸고 조회
        AccountInfo account = accountRepository.findByIdForUpdate(accountKey)
                .orElseThrow(() -> new IllegalArgumentException("계좌를 찾을 수 없습니다."));

        log.info("test1");
        // 2. 잔액 확인
        if (account.getTotalAmt() < useAmt) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }

        // 3. 차감
        long remain = account.getTotalAmt() - useAmt;
        account.setTotalAmt(remain);
        accountRepository.save(account);
    }

    /**
     * ======================================================
     * ① 낙관적 락 (Optimistic Lock)
     * ------------------------------------------------------
     * - version 컬럼을 사용하여 충돌 감지
     * - UPDATE 시 WHERE version=? 조건 추가
     * - 충돌 발생 시 OptimisticLockException 발생
     * ======================================================
     */
    @Transactional
    public void useAmountOptimistic(UseRequest request) {
        String accountKey = request.getAccountKey();
        long useAmt = request.getAmount();

        // 1. 계좌 조회
        AccountInfo account = accountRepository.findById(accountKey)
                .orElseThrow(() -> new IllegalArgumentException("계좌를 찾을 수 없습니다."));

        log.info("{} - 조회: version={}, totalAmt={}",
                Thread.currentThread().getName(), account.getVersion(), account.getTotalAmt());

        // 2. 잔액 확인
        if (account.getTotalAmt() < useAmt) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }

        // 3. 차감
        long remain = account.getTotalAmt() - useAmt;
        account.setTotalAmt(remain);

        // 4. version 체크 및 저장
        try {
            accountRepository.save(account); // 여기서 version check 발생
            log.info("{} - UPDATE 완료 후 version={}", Thread.currentThread().getName(), account.getVersion());
            // entityManager.flush();
            // log.info("test11");
        } catch (OptimisticLockException e) {
            log.warn("{} - 낙관적락 충돌 발생: {}", Thread.currentThread().getName(), e.getMessage());
            throw e;
        }
    }


    @Transactional
    public int useAmountOptimisticTest(String accountKey, long useAmt) {


        // 1. 계좌 조회
        AccountInfo account = accountRepository.findById(accountKey)
                .orElseThrow(() -> new IllegalArgumentException("계좌를 찾을 수 없습니다."));

        log.info("{} - 조회: version={}, totalAmt={}",
                Thread.currentThread().getName(), account.getVersion(), account.getTotalAmt());

        // 2. 잔액 확인
        if (account.getTotalAmt() < useAmt) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }

        // 3. 차감
        long remain = account.getTotalAmt() - useAmt;
        account.setTotalAmt(remain);

        // 4. version 체크 및 저장
        try {
            accountRepository.save(account); // 여기서 version check 발생
            log.info("{} - UPDATE 완료 후 version={}", Thread.currentThread().getName(), account.getVersion());
        } catch (OptimisticLockException e) {
            log.warn("{} - 낙관적락 충돌 발생: {}", Thread.currentThread().getName(), e.getMessage());
            throw e;
        }

        return (int) account.getTotalAmt().longValue();
    }
}