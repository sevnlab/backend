package com.example.backend.study.redis.dto;

import java.util.List;

public record ItemPageResponse(List<ItemResponse> items, long count) {


    public static ItemPageResponse fromResponse(List<ItemResponse> items, long count) {
        return new ItemPageResponse(items, count);
    }

    public static ItemPageResponse from(List<Item> items, long count) {

        // 각 Item 을 ItemResponse 로 변환
        return fromResponse(items.stream().map(ItemResponse::from).toList(), count);
    }
}
