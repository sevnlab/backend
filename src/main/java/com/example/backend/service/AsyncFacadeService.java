package com.example.backend.service;

import com.example.backend.dto.AsyncStartEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;


/**
 * 발행자(publisher) 역할 서비스
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncFacadeService {

    // 스프링 이벤트 기반 비동기 트리거
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 비동기 이벤트 발행
     */

    public void publishAsyncEvent(String jobName) {

        // 이벤트 페이로드(jobName 등 정보 전달성 객체)
        AsyncStartEventDto event = new AsyncStartEventDto(jobName);
        // 스프링 컨텍스트 내 이벤트 브로드캐스트, 비동기 리스너 존재 시 별도 쓰레드에서 수행
        eventPublisher.publishEvent(event);
    }
}
