package com.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.redisson.api.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/redis")
public class RedisTestController {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;

    // 1) 기본 get/set 테스트
    @GetMapping("/test")
    public String test() {
        stringRedisTemplate.opsForValue().set("hello", "world");
        return stringRedisTemplate.opsForValue().get("hello");
    }

    @GetMapping("/get")
    public String getValue() {
        return stringRedisTemplate.opsForValue().get("hello");
    }

    // 2) Cluster 라우팅 정상 확인
    @GetMapping("/cluster/hash")
    public String clusterHashTest(@RequestParam String key) {
        stringRedisTemplate.opsForValue().set(key, "value-" + key);
        return "Saved at cluster slot of key=" + key;
    }

    // 3) Redisson 기본 동작
    @GetMapping("/redisson/basic")
    public String redissonBasic() {
        RBucket<String> bucket = redissonClient.getBucket("rds:bucket");
        bucket.set("hello redisson");
        return bucket.get();
    }


    // 4) 분산락 테스트
    @GetMapping("/lock")
    public String lockTest() {
        RLock lock = redissonClient.getLock("my-lock");

        try {
            if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                Thread.sleep(3000); // 3초 작업
                return "Locked and executed!";
            } else {
                return "Another node is locking!";
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    // 5) Pub/Sub Publish
    @GetMapping("/pub")
    public String publishMessage(@RequestParam String msg) {
        RTopic topic = redissonClient.getTopic("chat");
        topic.publish(msg);
        return "published: " + msg;
    }


    // 6) MapCache (TTL 캐시)
    @GetMapping("/cache")
    public String cacheTest() {
        RMapCache<String, String> cache = redissonClient.getMapCache("cache:map");
        cache.put("k1", "v1", 10, TimeUnit.SECONDS);
        return "Cache saved (10s TTL)";
    }


    // 7) AtomicLong 테스트
    @GetMapping("/counter")
    public String counter() {
        RAtomicLong counter = redissonClient.getAtomicLong("global:count");
        long v = counter.incrementAndGet();
        return "counter = " + v;
    }


    // 8) Bloom Filter 테스트
    @GetMapping("/bloom")
    public String bloomTest(@RequestParam String key) {
        RBloomFilter<String> bloom = redissonClient.getBloomFilter("bloom:test");

        bloom.tryInit(10000, 0.01); // 예상 1만건, 오차 1%

        boolean added = bloom.add(key);
        boolean exists = bloom.contains(key);

        return "added=" + added + ", exists=" + exists;
    }
}
