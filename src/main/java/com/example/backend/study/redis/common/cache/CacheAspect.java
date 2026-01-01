package com.example.backend.study.redis.common.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

/**
 *
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CacheAspect {
    private final List<CacheHandler> cacheHandlers;
    private final CacheKeyGenerator cacheKeyGenerator;

    /**
     * cacheable 어노테이션을 처리하는 메소드
     */
    @Around("@annotation(customCacheable)")
    public Object handleCacheable(ProceedingJoinPoint joinPoint, CustomCacheable customCacheable) {
        CacheStrategy cacheStrategy = customCacheable.cacheStrategy();

        // 캐시 핸들러를 찾는다.
        CacheHandler cacheHandler = findCacheHandler(cacheStrategy);

        // 캐시 키 생성
        String key = cacheKeyGenerator.genKey(joinPoint, cacheStrategy, customCacheable.cacheName(), customCacheable.key());
        Duration ttl = Duration.ofSeconds(customCacheable.ttlSeconds());

        // JoinPoint 를 이용해서 데이터를 꺼내온다
        Supplier<Object> dataSourceSupplier = createDataSourceSupplier(joinPoint);
        Class returnType = findReturnType(joinPoint);

        try {
            // cacheHandler 핸들러를 통해서 실제 내부 로직 처리
            log.info("[CacheAspect.handleCacheable] key={}", key);
            return cacheHandler.fetch(
                    key,
                    ttl,
                    dataSourceSupplier,
                    returnType
            );
        } catch (Exception e) {
            // 예외 발생시 데이터소스에서 원본데이터를 재요청
            log.error("[CacheAspect.handleCacheable] key={}", key, e);
            return dataSourceSupplier.get();
        }
    }

    // 전략별로 캐시핸들러가 만들어지는데 cacheStrategy 를 통해서 cacheHandlers 리스트를 순회하면서
    // 실제 구현체에 매핑되는것을 찾아옴.
    private CacheHandler findCacheHandler(CacheStrategy cacheStrategy) {
        return cacheHandlers.stream()
                .filter(cacheHandler -> cacheHandler.supports(cacheStrategy)) // 해당 핸들러가 해당전략을 지원하는지 확인
                .findFirst()
                .orElseThrow();
    }

    // ProceedingJoinPoint 를 이용해 실제 메소드가 호출되는것을 가로채온다.
    // Supplier 를 통해서 콜백으로 핸들러내에서 호출되는거라 람다로 정의해야한다.
    private Supplier<Object> createDataSourceSupplier(ProceedingJoinPoint joinPoint) {
        return () -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }

    // MethodSignature를 꺼내 이 메서드가 어떤 반환타입을 가지고있는지 확인
    private Class findReturnType(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;

        return methodSignature.getReturnType();
    }

    // 반환값을 캐시에 갱신하는 메서드
    // AfterReturning 선언으로 반환값을 가져올수 있다.
    @AfterReturning(pointcut = "@annotation(cachePut)", returning = "result")
    public void handleCachePut(JoinPoint joinPoint, CustomCachePut cachePut, Object result) {
        CacheStrategy cacheStrategy = cachePut.cacheStrategy();
        CacheHandler cacheHandler = findCacheHandler(cacheStrategy);
        String key = cacheKeyGenerator.genKey(joinPoint, cacheStrategy, cachePut.cacheName(), cachePut.cacheName());
        log.info("[CacheAspect.handleCachePut] key={}", key);
        cacheHandler.put(key, Duration.ofSeconds(cachePut.ttlSeconds()), result);
    }

    @AfterReturning(pointcut = "@annotation(cacheEvict)")
    public void handleCacheEvict(JoinPoint joinPoint, CustomCacheEvict cacheEvict) {
        CacheStrategy cacheStrategy = cacheEvict.cacheStrategy();
        CacheHandler cacheHandler = findCacheHandler(cacheStrategy);
        String key = cacheKeyGenerator.genKey(joinPoint, cacheStrategy, cacheEvict.cacheName(), cacheEvict.cacheName());
        log.info("[CacheAspect.handleCacheEvict] key={}", key);
        cacheHandler.evict(key);
    }




}
