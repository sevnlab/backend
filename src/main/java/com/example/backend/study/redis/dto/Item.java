package com.example.backend.study.redis.dto;

import lombok.Getter;
import lombok.ToString;

import java.util.concurrent.atomic.AtomicLong;

@Getter
@ToString
public class Item {
    private Long itemId; // 식별자
    private String data;

    // itemId 를 채번하기 위해 AtomicLong(동시성에서 thread-safe 한 클래스)  정의
    private static final AtomicLong NEXT_ID = new AtomicLong();


    public static Item create(ItemCreateRequest request) {
        Item item = new Item();

        item.itemId = NEXT_ID.incrementAndGet(); // create 메소드가 호출될 때마다 ID 발급
        item.data = request.data();
        return item;
    }

    public void update(ItemUpdateRequest request) {
        this.data = request.data();
    }

}
