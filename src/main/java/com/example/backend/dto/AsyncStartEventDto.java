package com.example.backend.dto;

import lombok.Data;


/**
 * 시작 이벤트
 * - 어떤 job을 돌릴지 (jobName)
 */
@Data
public class AsyncStartEventDto {
    private final String jobName;
}
