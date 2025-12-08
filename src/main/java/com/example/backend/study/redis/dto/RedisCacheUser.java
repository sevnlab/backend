package com.example.backend.study.redis.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RedisCacheUser {
    private Long id;
    private String name;
    private int age;
}
