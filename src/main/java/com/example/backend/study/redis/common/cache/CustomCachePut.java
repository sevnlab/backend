package com.example.backend.study.redis.common.cache;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // 메서드만 허용
@Retention(RetentionPolicy.RUNTIME) // 실행중 유지
public @interface CustomCachePut {
    CacheStrategy cacheStrategy(); // 캐시 전략
    String cacheName(); // 캐시 이름

    String key(); // 캐시 키
    long ttlSeconds(); // 캐시 유지 시간(초)
}
