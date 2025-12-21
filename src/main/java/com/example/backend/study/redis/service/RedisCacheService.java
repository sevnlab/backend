package com.example.backend.study.redis.service;

import com.example.backend.study.redis.entity.RedisCacheUser;
import com.example.backend.study.redis.repository.RedisCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisCacheService {

    private final RedisCacheRepository userRepository;

    @Cacheable(value = "userCache", key = "#name", unless = "#result == null")
    public RedisCacheUser getUser(String name) {
        return userRepository.findByName(name).orElse(null);
//        return userRepository.findByName(name)
//                .orElseThrow(() -> new RuntimeException("NOT FOUND"));
    }

    @CachePut(value = "userCache", key = "#user.name")
    public RedisCacheUser updateUser(RedisCacheUser user) {

        RedisCacheUser entity = userRepository.findByName(user.getName())
                .orElseThrow(() -> new RuntimeException("NOT FOUND"));

        entity.setAge(user.getAge());
        entity.setEmail(user.getEmail());

        userRepository.save(entity); // 이 값이 캐시에 다시 저장됨

        return entity;
    }

//    @CacheEvict(value = "userCache", key = "#name")
    @CacheEvict(value = "userCache", key = "#name", allEntries=true)
    public void deleteUser(String name) {
//        RedisCacheUser entity = userRepository.findByName(name)
//                .orElseThrow(() -> new RuntimeException("NOT FOUND"));
//
//        userRepository.delete(entity);
    }


}
