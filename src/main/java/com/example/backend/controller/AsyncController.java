package com.example.backend.controller;


import com.example.backend.service.AsyncFacadeService;
import com.example.backend.service.AsyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/migration")
@RequiredArgsConstructor
public class AsyncController {
    private final AsyncFacadeService asyncFacadeService;

    // 비동기 업데이트 실행
    @PostMapping("/update-async")
    public String updateAsync() {
        String jobName = "ASYNC_TEST";
        asyncFacadeService.publishAsyncEvent(jobName);
        return "비동기 마이그레이션 이벤트가 발행되었습니다. (jobName=" + jobName + ")";
    }
}
