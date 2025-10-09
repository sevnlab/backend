package com.example.backend.service;

import com.example.backend.dto.AsyncStartEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncFacadeService {
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 비동기 이벤트 발행
     */

    public void publishAsyncEvent(String jobName) {
        AsyncStartEventDto event = new AsyncStartEventDto(jobName);
        eventPublisher.publishEvent(event);
    }
}
