package com.example.backend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class UsersPrimaryKey implements Serializable {
    // JPA에서 복합 기본 키 클래스는 직렬화가 가능해야 합니다, JPA 요구사항
    private String email = "";

    public UsersPrimaryKey() {
        // 기본 생성자 추가
    }
}
