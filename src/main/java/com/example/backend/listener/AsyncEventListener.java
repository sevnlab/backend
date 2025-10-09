package com.example.backend.listener;


import com.example.backend.dto.AsyncStartEventDto;
import com.example.backend.service.AsyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 비동기 이벤트 처리, 알림, 로그, 배치 트리거
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncEventListener {
    private final AsyncService asyncService;


    @Async("updateExecutor") // 여기서 Async 를 붙여야 Controller 응답이 즉시 리턴
    @EventListener
    public void handleEvent(AsyncStartEventDto event) {
        log.info("비동기 이벤트 수신: {}", event.getJobName());

        // 별도 스레드에서 비동기로 시작
        asyncService.startAsyncUpdate();
    }

}
