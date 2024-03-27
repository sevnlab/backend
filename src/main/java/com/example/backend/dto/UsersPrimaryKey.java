package com.example.backend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@ToString
public class UsersPrimaryKey implements Serializable {
    @Serial
    private static final long serialVersionUID = -7890153241067473954L;
    // JPA에서 복합 기본 키 클래스는 직렬화가 가능해야 합니다, JPA 요구사항
    private String userId = "";

    public UsersPrimaryKey() {
        // 기본 생성자 추가
    }
}
