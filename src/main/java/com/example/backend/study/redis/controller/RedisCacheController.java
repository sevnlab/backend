package com.example.backend.study.redis.controller;

import com.example.backend.study.redis.entity.RedisCacheUser;
import com.example.backend.study.redis.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/redis/user")
public class RedisCacheController {

    private final RedisCacheService service;

    @PostMapping("/cacheable")
    public RedisCacheUser getUser(String name) {
        return service.getUser(name);
    }

    @PostMapping("/cacheput")
    public RedisCacheUser update(@RequestBody RedisCacheUser user) {
        return service.updateUser(user);
    }

    @PostMapping("/cacheEvict")
    public void delete(@RequestParam String name) {
        service.deleteUser(name);
    }
}
