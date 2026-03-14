package com.example.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // 스케쥴링 활성화
@ComponentScan(
    basePackages = "com.example.backend",
    excludeFilters = @ComponentScan.Filter(
        // study.redis 패키지는 Redis 학습/테스트용 코드이므로 운영 빈 등록에서 제외
        // 해당 패키지의 클래스들은 RedisTemplate 등 Redis 빈에 의존하기 때문에
        // local 프로파일처럼 Redis가 없는 환경에서 스캔되면 앱이 뜨지 않음
        type = FilterType.REGEX,
        pattern = "com\\.example\\.backend\\.study\\.redis\\..*"
    )
)
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
