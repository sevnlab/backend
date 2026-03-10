package com.example.backend.service;

import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Redis 기반 로그인 시도 횟수 관리 (운영 환경 전용)
 *
 * Redis Key 구조:
 *   login:fail:{memberId} → 실패 횟수 (1~3), TTL 24시간
 *
 * 동작 방식:
 *   - 실패 시: 횟수 +1, 첫 실패 시 TTL 24시간 설정
 *   - 성공 시: 키 삭제 (횟수 초기화)
 *   - 3회 실패 시: 24시간 동안 로그인 불가
 *   - 24시간 후: Redis TTL 만료로 자동 해제 (별도 처리 불필요)
 *
 * 2대 이상 서버에서도 Redis를 공유하므로 서버 간 횟수가 동기화됨
 */
@Service
@Profile("real")
public class RedisLoginAttemptService implements LoginAttemptService {

    private static final String FAIL_KEY_PREFIX = "login:fail:";
    private static final int MAX_FAIL_COUNT = 3;
    private static final long LOCK_DURATION_HOURS = 24;

    private final StringRedisTemplate redisTemplate;

    public RedisLoginAttemptService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void loginFailed(String memberId) {
        String key = FAIL_KEY_PREFIX + memberId;

        // setIfAbsent: 키가 없을 때만 "0" 으로 초기화하면서 TTL 24시간 동시 설정
        // expire() 를 별도 호출하면 Redisson pExpire 버그로 StackOverflowError 발생하므로
        // TTL 은 최초 키 생성 시점에 SET key 0 EX 86400 NX 명령으로 한 번에 처리
        redisTemplate.opsForValue().setIfAbsent(key, "0", LOCK_DURATION_HOURS, TimeUnit.HOURS);

        // 이후 INCR 로 횟수 증가 (TTL 은 유지됨)
        redisTemplate.opsForValue().increment(key);
    }

    @Override
    public void loginSucceeded(String memberId) {
        // 로그인 성공 시 실패 횟수 초기화
        redisTemplate.delete(FAIL_KEY_PREFIX + memberId);
    }

    @Override
    public boolean isLocked(String memberId) {
        String value = redisTemplate.opsForValue().get(FAIL_KEY_PREFIX + memberId);

        if (value == null) return false;

        return Integer.parseInt(value) >= MAX_FAIL_COUNT;
    }
}