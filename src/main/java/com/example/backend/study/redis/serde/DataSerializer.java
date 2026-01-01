package com.example.backend.study.redis.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 객체를 문자열로 직렬롸, 역직렬화 하는 유틸성 클래스
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataSerializer {
    private static final ObjectMapper objectMapper = new ObjectMapper();


    // 객체 데이터 직렬화
    public static String serializeOrException(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            log.error("[DataSerializer.serializeOrException] data={}", data, e);
            throw  new RuntimeException(e);
        }
    }

    // 직렬화된 파라미터를 받아서 클래스 타입의 객체로 변환
    public static <T> T deserializeOrNull(String data, Class<T> clazz) {
        try {
            return objectMapper.readValue(data, clazz);
        } catch (Exception e) {
            log.error("[DataSerializer.deserializeOrNull] data={}", data, e);
            return null;
        }
    }
}
