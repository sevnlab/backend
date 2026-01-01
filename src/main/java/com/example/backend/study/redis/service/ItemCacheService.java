package com.example.backend.study.redis.service;


import com.example.backend.study.redis.common.cache.CacheStrategy;
import com.example.backend.study.redis.dto.ItemCreateRequest;
import com.example.backend.study.redis.dto.ItemPageResponse;
import com.example.backend.study.redis.dto.ItemResponse;
import com.example.backend.study.redis.dto.ItemUpdateRequest;

public interface ItemCacheService {
    ItemResponse read(Long itemId);

    ItemPageResponse readAll(Long page, Long pageSize);

    ItemPageResponse readAllInfiniteScroll(Long lastItemId, Long pageSize);

    ItemResponse create(ItemCreateRequest request);

    ItemResponse update(Long itemId, ItemUpdateRequest request);

    void delete(Long itemId);

    // 서비스 구현체인지 확인하는 supports 메서드
    boolean supports(CacheStrategy cacheStrategy);
}
