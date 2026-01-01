package com.example.backend.study.redis.service.strategy.none;

import com.example.backend.study.redis.common.cache.CacheStrategy;
import com.example.backend.study.redis.common.cache.CustomCacheEvict;
import com.example.backend.study.redis.common.cache.CustomCachePut;
import com.example.backend.study.redis.common.cache.CustomCacheable;
import com.example.backend.study.redis.dto.ItemCreateRequest;
import com.example.backend.study.redis.dto.ItemPageResponse;
import com.example.backend.study.redis.dto.ItemResponse;
import com.example.backend.study.redis.dto.ItemUpdateRequest;
import com.example.backend.study.redis.service.ItemCacheService;
import com.example.backend.study.redis.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 커스텀 캐시애노테이션을 여기에 적용
 * ItemCacheService(캐시 전략 처리) 가 캐시관련된 로직을 처리하고 내부적으로  ItemService를 호출해서 비즈니스로직 처리
 */
@Service
@RequiredArgsConstructor
public class ItemNoneCacheService implements ItemCacheService {
    private final ItemService itemService;


    @Override
    @CustomCacheable(cacheStrategy = CacheStrategy.NONE, cacheName = "item", key = "#itemId", ttlSeconds = 5)
    public ItemResponse read(Long itemId) {
        return itemService.read(itemId);
    }

    @Override
    @CustomCacheable(cacheStrategy = CacheStrategy.NONE, cacheName = "itemList", key = "#page + ':' + #pageSize", ttlSeconds = 5)
    public ItemPageResponse readAll(Long page, Long pageSize) {
        return itemService.readAll(page, pageSize);
    }

    @Override
    @CustomCacheable(cacheStrategy = CacheStrategy.NONE, cacheName = "itemListInfiniteScroll", key = "#lastItemId + ':' + #pageSize", ttlSeconds = 5)
    public ItemPageResponse readAllInfiniteScroll(Long lastItemId, Long pageSize) {
        return itemService.readAllInfiniteScroll(lastItemId, pageSize);
    }

    @Override
    public ItemResponse create(ItemCreateRequest request) {
        return itemService.create(request);
    }

    @Override
    @CustomCachePut(cacheStrategy = CacheStrategy.NONE, cacheName = "item", key = "#itemId", ttlSeconds = 5)
    public ItemResponse update(Long itemId, ItemUpdateRequest request) {
        return itemService.update(itemId, request);
    }

    @Override
    @CustomCacheEvict(cacheStrategy = CacheStrategy.NONE, cacheName = "item", key = "#itemId")
    public void delete(Long itemId) {
        itemService.delete(itemId);
    }

    @Override
    public boolean supports(CacheStrategy cacheStrategy) {
        return CacheStrategy.NONE == cacheStrategy;
    }
}
