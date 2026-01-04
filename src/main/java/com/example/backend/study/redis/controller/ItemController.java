package com.example.backend.study.redis.controller;

import com.example.backend.study.redis.common.cache.CacheStrategy;
import com.example.backend.study.redis.dto.ItemCreateRequest;
import com.example.backend.study.redis.dto.ItemPageResponse;
import com.example.backend.study.redis.dto.ItemResponse;
import com.example.backend.study.redis.dto.ItemUpdateRequest;
import com.example.backend.study.redis.service.ItemCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ItemController {
    private final List<ItemCacheService> itemCacheServices;

    @GetMapping("/cache-strategy/{cacheStrategy}/items/{itemId}")
    public ItemResponse read(@PathVariable CacheStrategy cacheStrategy, @PathVariable Long itemId) {

        return resolveCacheHandler(cacheStrategy).read(itemId);
    }

    @GetMapping("/cache-strategy/{cacheStrategy}/items")
    public ItemPageResponse read(
            @PathVariable CacheStrategy cacheStrategy,
            @PathVariable Long page,
            @PathVariable Long pageSize
    ) {

        return resolveCacheHandler(cacheStrategy).readAll(page, pageSize);
    }

    @GetMapping("/cache-strategy/{cacheStrategy}/items/infinite-scroll")
    public ItemPageResponse readInfiniteScroll(
            @PathVariable CacheStrategy cacheStrategy,
            @PathVariable(required = false) Long lastItemId, //첫페이지는 null
            @PathVariable Long pageSize
    ) {

        return resolveCacheHandler(cacheStrategy).readAllInfiniteScroll(lastItemId, pageSize);
    }

    @PostMapping("/cache-strategy/{cacheStrategy}/items")
    public ItemResponse create(
            @PathVariable CacheStrategy cacheStrategy,
            @RequestBody ItemCreateRequest request
            ) {

        return resolveCacheHandler(cacheStrategy).create(request);
    }

    @PostMapping("/cache-strategy/{cacheStrategy}/items/{itemId}")
    public ItemResponse update(
            @PathVariable CacheStrategy cacheStrategy,
            @PathVariable Long itemId,
            @RequestBody ItemUpdateRequest request
    ) {

        return resolveCacheHandler(cacheStrategy).update(itemId, request);
    }

    @DeleteMapping("/cache-strategy/{cacheStrategy}/items/{itemId}")
    public void delete(
            @PathVariable CacheStrategy cacheStrategy,
            @PathVariable Long itemId
    ) {

        resolveCacheHandler(cacheStrategy).delete(itemId);
    }


    // cacheStrategy 파라미터가 실제구현된 캐시 서비스 구현체를 찾는다.
    private ItemCacheService resolveCacheHandler(CacheStrategy cacheStrategy) {
        return itemCacheServices.stream()
                .filter(itemCacheService -> itemCacheService.supports(cacheStrategy))
                .findFirst()
                .orElseThrow();
    }

}
