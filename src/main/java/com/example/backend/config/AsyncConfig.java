package com.example.backend.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 비동기 스레드 설정
 * CorePoolSize 기본 스레드가 모두 사용중이면 새 작업은 큐(QueueCapacity) 로 이동,
 * 큐(QueueCapacity) 가 가득 찰 경우 MaxPoolSize 만큼 스레드가 늘어남
 * 큐(QueueCapacity) 도 가득차고, MaxPoolSize도 모두 사용중이라면 RejectedExecutionException 예외 발생
 */
@Configuration
@EnableAsync // Async 메서드를 별도 스레드로 실행 가능, 필수설정
public class AsyncConfig {

    /**
     * DB I/O 허용량 기준으로 병렬도 결정하는 법
     * 1. 싱글 스레드로 1000건 업데이트 시간을 측정.
     * 2. 병렬도를 점차 늘려서 처리속도 향상 비율이 1.5배 이하로 떨어지는 지점이 I/O 허용 한계
     */

    @Bean(name = "updateExecutor")
    public Executor updateExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        /**
         * 동시에 실행할 기본 스레드 수
         * CPU 코어 개수와 비슷하게 하는게 적절
         */
        executor.setCorePoolSize(4);

        /** 큐가 가득 찼을때 추가로 확장 가능한 최대 스레드 수
         * DB 과부하 방지용 제한, CPU 코어 개수 2배정도가 적절
         * DB I/O 여유가 많으면 좀더 높여도 무방, 동시 커밋/락 경쟁이 우려된다면 낮춰야함
         * */
        executor.setMaxPoolSize(8);

        /**
         * 대기 큐 용량
         * 해당개수만큼 비동기 작업대기 가능, 너무 크면 메모리 점유율 높아짐
         * 청크 총 개수 ~500개  100~200 권장
         * 청크 총 개수 1000~3000개 300~500 권장
         * 청크 총 개수 1만개 이상 1000 정도(초과는 비효율)
         */
        executor.setQueueCapacity(500);

        /**
         * 로그 확인 시 스레드 식별, 디버깅시 사용
         * 로그에 지정한 이름 [Async-Update-1], [Async-Update-2] 형태로 찍힘
         */
        executor.setThreadNamePrefix("Async-Update-");
        executor.initialize();
        return executor;
    }
}

