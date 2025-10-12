package com.example.backend.service;

import com.example.backend.util.SystemUtil;
import com.sun.management.OperatingSystemMXBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 데이터를 비동기 병렬로 업데이트하는 서비스
 * -------------------------------------------------------------
 * - 파일에서 PK 목록을 읽고
 * - CHUNK_SIZE 단위로 DB 업데이트
 * - ThreadPool 기반 비동기 처리 및 진행률 모니터링
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncService {
    private final MigrationService migrationService;

    /** 대량 업데이트 대상 파일 경로 */
    private static final Path FILE_PATH = Path.of("H:\\work_space\\uploadFile\\ASYNC_TEST_ROWID.txt");

    /** 청크 크기 (한 번에 업데이트할 단위) */
    private static final int CHUNK_SIZE = 1000;

    /** 모니터링 로그 주기 (초) */
    private static final int LOG_INTERVAL_SEC = 5;

    /** 상태 관리 변수 (여러 비동기 스레드가 동시에 완료 카운트를 늘려도 안전하게 처리)
     * processedCount: 병렬 스레드들이 동시에 증가시켜도 경쟁(race condition) 없이 thread-safe.
     * */
    private final AtomicInteger processedCount = new AtomicInteger(0);
    private int totalCount = 0;

    private final OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();


    /**
     * 비동기 대량 업데이트 시작
     */
    public void startAsyncUpdate() {
        SystemUtil.printStatus("비동기 대량 업데이트 시작");

        Instant start = Instant.now();
//        try {

//            totalCount = (int) Files.lines(FILE_PATH).count();
//            log.info("총 {}건의 데이터 확인됨", totalCount);
//            SystemUtil.printStatus("파일 건수 계산 완료");


            /**
             * Files.lines()는 내부적으로 BufferedReader를 사용, 공백 제거, "" 필터링
             * */
            try (Stream<String> lines = Files.lines(FILE_PATH)) {

                // [1] 파일 전체 읽기 (1회)
                List<String> allLines = lines.map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
                totalCount = allLines.size();
                log.info("총 {}건의 데이터 확인됨", totalCount);
                SystemUtil.printStatus("파일 읽기 완료");

                // [2] 비동기 청크 처리 시작
                submitChunksInBatches(allLines);

                // [3] 모니터링 스케줄러 시작
                startMonitoring();

            } catch (IOException e) {
                log.error("파일 읽기 실패: {}", FILE_PATH, e);
            }

            // 2?? BufferedReader로 한 줄씩 읽어서 처리
//            try (BufferedReader reader = Files.newBufferedReader(FILE_PATH)) {
//                List<String> buffer = new ArrayList<>(CHUNK_SIZE);
//                String line;
//
//                while ((line = reader.readLine()) != null) {
//                    buffer.add(line.trim());
//                    if (buffer.size() == CHUNK_SIZE) {
//
//
//                        submitChunk(new ArrayList<>(buffer)); // 복사본 넘김
//                        buffer.clear();
//                    }
//                }
//
//                if (!buffer.isEmpty()) submitChunk(new ArrayList<>(buffer));
//            }

//            SystemUtil.printStatus("전체 비동기 작업 등록 완료");
//            startMonitoringThread();

//        } catch (IOException e) {
//            log.error("파일 읽기 실패: {}", FILE_PATH, e);
//        }

        Instant end = Instant.now();
        log.info("전체 작업 등록 완료 (총 소요: {}초)", Duration.between(start, end).toSeconds());
    }

    /**
     * 파일 데이터를 CHUNK_SIZE 단위로 나누어 비동기 업데이트 수행
     */
    private void submitChunksInBatches(List<String> allLines) {
        int chunkCount = (int) Math.ceil((double) totalCount / CHUNK_SIZE);
        log.info("총 {}개의 청크로 분할하여 업데이트 시작", chunkCount);

        /** 병렬 태스크 과부하 방지용 제한 큐 (최대 20개 동시 실행)
         * 현재 CorePoolSize 가 20보다 작으면 의미없는 코드
         * */
        Semaphore semaphore = new Semaphore(20);

        for (int i = 0; i < totalCount; i += CHUNK_SIZE) {
            int end = Math.min(i + CHUNK_SIZE, totalCount);

            /** new ArrayList<>(subList()) 로 참조 복사 → GC가 원본 리스트를 유지하지 않도록 함 */
            List<String> chunk = new ArrayList<>(allLines.subList(i, end));

            try {
                /**
                 * 슬롯 확보, 동시 실행 개수 제한 유지
                 * updateChunkAsync() 완료 시 release() 다음 청크 실행
                 * */

                semaphore.acquire();
                updateChunkAsync(chunk)
                        .whenComplete((res, ex) -> semaphore.release());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Semaphore 대기 중 인터럽트 발생", e);
                break;
            }
        }
        SystemUtil.printStatus("전체 비동기 청크 등록 완료");
    }



    /**
     * 실제 비동기 업데이트 실행
     * - AsyncConfig에서 정의한 ThreadPoolTaskExecutor("updateExecutor") 사용
     * - Oracle DB 부하를 최소화하기 위해 병렬 청크 제한
     */
    @Async("updateExecutor")
    public CompletableFuture<Void> updateChunkAsync(List<String> pkList) {
        try {
            migrationService.updateChunk(pkList);

            /** 완료건수 누적(AtomicInteger 사용으로 thread-safe) */
            int done = processedCount.addAndGet(pkList.size());

            /** 10만 건 단위로만 로그 출력 (너무 잦은 로그 I/O 부하 방지) */
            if (done % 100_000 == 0 || done == totalCount) {
                log.info("진행 상황: {} / {} ({}%)",
                        done, totalCount, String.format("%.2f", (done * 100.0 / totalCount)));
            }
        } catch (Exception e) {
            log.error("업데이트 실패 (청크 크기: {})", pkList.size(), e);
        }
        return CompletableFuture.completedFuture(null);
    }

    /** 청크를 비동기 큐로 등록 */
    private void submitChunk(List<String> chunk) {
        // ? 복사본으로 넘겨 메모리 참조 끊기 (GC가 회수 가능)
        updateChunkAsync(new ArrayList<>(chunk));
    }

//    private void startMonitoringThread() {
//        Thread monitor = new Thread(() -> {
//            long prev = 0;
//            Instant prevTime = Instant.now();
//
//            while (processedCount.get() < totalCount) {
//                try {
//                    Thread.sleep(LOG_INTERVAL_SEC * 1000L);
//                    long now = processedCount.get();
//                    Instant nowTime = Instant.now();
//
//                    long diff = now - prev;
//                    double seconds = Duration.between(prevTime, nowTime).toMillis() / 1000.0;
//                    double rate = diff / seconds;
//                    double jvmCpu = osBean.getProcessCpuLoad() * 100;
//                    double sysCpu = osBean.getSystemCpuLoad() * 100;
//                    double progress = (now * 100.0) / totalCount;
//
//                    log.info(String.format(
//                            "진행률: %.2f%% (%d/%d), 처리속도: %.1f건/초, JVM CPU: %.1f%%, SYS CPU: %.1f%%",
//                            progress, now, totalCount, rate, jvmCpu, sysCpu));
//
//                    prev = now;
//                    prevTime = nowTime;
//
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                    break;
//                }
//            }
//
//            log.info("? 전체 업데이트 완료! 총 {}건 처리.", processedCount.get());
//        });
//        monitor.setDaemon(true);
//        monitor.start();
//    }

    /**
     * 실시간 모니터링 스케줄러
     * - 5초마다 JVM/시스템 CPU, 처리속도, 진행률 출력
     * 서비스 작업의 진행률을 직접 추적하고 있으므로 유틸로 분리하지 않고 해당서비스에서 같이 사용.
     * 분리하게 되면 매번 불러서 써야함.
     */
    private void startMonitoring() {

        /** 단일 스레드로 주기적인 모니터링 작업을 실행할 스케줄러 생성 */
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        final long[] prevCount = {0};
        final Instant[] prevTime = {Instant.now()};

        /** 0초 후 즉시 실행 → 이후 5초 간격으로 반복 실행 */
        scheduler.scheduleAtFixedRate(() -> {
            long now = processedCount.get();
            Instant nowTime = Instant.now();
            double seconds = Duration.between(prevTime[0], nowTime).toMillis() / 1000.0;
            long diff = now - prevCount[0];
            double rate = diff / seconds;
            double jvmCpu = osBean.getProcessCpuLoad() * 100;
            double sysCpu = osBean.getSystemCpuLoad() * 100;
            double progress = (now * 100.0) / totalCount;

            log.info(String.format(
                    "[모니터링] 진행률: %.2f%% (%d/%d), 처리속도: %.1f건/초, JVM CPU: %.1f%%, SYS CPU: %.1f%%",
                    progress, now, totalCount, rate, jvmCpu, sysCpu));

            prevCount[0] = now;
            prevTime[0] = nowTime;

            /** 모든 청크가 완료되면 모니터링 스레드를 종료, 리소스 정리 */
            if (now >= totalCount) {
                log.info("전체 업데이트 완료! 총 {}건 처리.", now);
                scheduler.shutdown();
            }
        }, 0, LOG_INTERVAL_SEC, TimeUnit.SECONDS);
    }
}