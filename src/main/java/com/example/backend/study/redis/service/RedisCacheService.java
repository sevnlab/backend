package com.example.backend.study.redis.service;

import com.example.backend.study.redis.entity.RedisCacheUser;
import com.example.backend.study.redis.repository.RedisCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service("redisUserService")
@RequiredArgsConstructor
public class RedisCacheService {

    private final RedisCacheRepository userRepository;

    @Cacheable(value = "userCache", key = "#id")
    public RedisCacheUser getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("NOT FOUND"));
    }

    @CachePut(value = "userCache", key = "#user.id")
    public RedisCacheUser updateUser(RedisCacheUser user) {
        return userRepository.save(user);
    }

    @CacheEvict(value = "userCache", key = "#id")
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }


}
