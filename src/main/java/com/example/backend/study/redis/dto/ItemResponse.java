package com.example.backend.study.redis.dto;

public record ItemResponse(Long itemId, String data) {

    public static ItemResponse from(Item item) {
        return new ItemResponse(item.getItemId(), item.getData());
    }

}
