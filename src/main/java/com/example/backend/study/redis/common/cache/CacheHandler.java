package com.example.backend.study.redis.common.cache;


import java.time.Duration;
import java.util.function.Supplier;

/**
 * CacheStrategy 를 통해서 어떤 핸들러에 매핑되는지 찾기 위함
 */
public interface CacheHandler {

    // 키, ttl, datasourceSupplier(원본데이터를 가져오기) , clazz(응답데이터의 클래스 타입)
   <T> T fetch(String key, Duration ttl, Supplier<T> datasourceSupplier, Class<T> clazz);
   void put(String key, Duration ttl, Object value);
   void evict(String key);
   // 어떤 캐시전략을 사용하는지
   boolean supports(CacheStrategy cacheStrategy);
}
