package com.example.backend.study.redis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisTemplateTestController {

    private final RedisTemplate<String, Object> redisTemplate;

    /** -----------------------------
     * 1) Hash 备炼 历厘
     * ----------------------------- */
    @GetMapping("/hash/set")
    public String hashSet(@RequestParam String key,
                          @RequestParam String field,
                          @RequestParam String value) {

//        redisTemplate.opsForHash().put(key, field, value);
        redisTemplate.opsForHash().put("1000", "name", "park");
        redisTemplate.opsForHash().put("1000", "name", "min");
        redisTemplate.opsForHash().put("1000", "sports", "soccer");

        return "HASH SET: " + key + "[" + field + "]=" + value;
    }

    @GetMapping("/hash/get")
    public Object hashGet(@RequestParam String key,
                          @RequestParam String field) {

        System.out.println(redisTemplate.opsForHash().entries(key));
        return redisTemplate.opsForHash().get(key, field);
    }


    /** -----------------------------
     * 2) List 备炼
     * ----------------------------- */
    @GetMapping("/list/leftPush")
    public String listLeftPush(@RequestParam String key,
                               @RequestParam String value) {

        redisTemplate.opsForList().leftPush(key, value);
        return "LIST LPUSH: " + key + " <= " + value;
    }

    @GetMapping("/list/all")
    public Object listAll(@RequestParam String key) {


        return redisTemplate.opsForList().range(key, 0, -1);
    }


    /** -----------------------------
     * 3) Set 备炼
     * ----------------------------- */
    @GetMapping("/set/add")
    public String setAdd(@RequestParam String key,
                         @RequestParam String value) {

        redisTemplate.opsForSet().add(key, value);
        return "SET ADD: " + key + " <= " + value;
    }

    @GetMapping("/set/all")
    public Object setAll(@RequestParam String key) {
        return redisTemplate.opsForSet().members(key);
    }


    /** -----------------------------
     * 4) ZSet 备炼 (Sorted Set)
     * ----------------------------- */
    @GetMapping("/zset/add")
    public String zsetAdd(@RequestParam String key,
                          @RequestParam String value,
                          @RequestParam double score) {

        redisTemplate.opsForZSet().add(key, value, score);
        return "ZSET ADD: " + key + " <= (" + score + "," + value + ")";
    }

    @GetMapping("/zset/all")
    public Object zsetAll(@RequestParam String key) {

        return redisTemplate.opsForZSet().rangeWithScores(key, 0, -1);
    }


    /** -----------------------------
     * 5) Multi-Key (multiGet, multiSet)
     * ----------------------------- */
    @GetMapping("/multi/set")
    public String multiSet() {

        Map<String,Object> map = new HashMap<>();
        map.put("user:1", "kim");
        map.put("user:2", "lee");
        map.put("user:3", "park");

        redisTemplate.opsForValue().multiSet(map);

        return "MULTI SET OK";
    }

    @GetMapping("/multi/get")
    public Object multiGet() {

        List<String> keys = Arrays.asList("user:1", "user:2", "user:3");
        return redisTemplate.opsForValue().multiGet(keys);
    }
}
