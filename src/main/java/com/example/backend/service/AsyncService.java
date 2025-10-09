package com.example.backend.service;

import com.example.backend.util.SystemUtil;
import com.sun.management.OperatingSystemMXBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncService {

    private final MigrationService migrationService;



//    private static final String FILE_PATH = "H:\\work_space\\uploadFile\\ASYNC_TEST_202510091543.txt";

    // rowid
//    private static final String FILE_PATH = "H:\\work_space\\uploadFile\\ASYNC_TEST_ROWID.txt";
    private static final Path FILE_PATH = Path.of("H:\\work_space\\uploadFile\\ASYNC_TEST_ROWID.txt");
    private static final int CHUNK_SIZE = 1000; // 업데이트 단위(1000)
    private static final int LOG_INTERVAL_SEC = 5; // 모니터링 스레드가 5초마다 상태를 찍게 함.

    // 상태 관리용 변수
    private final AtomicInteger processedCount = new AtomicInteger(0); // 여러 비동기 스레드가 동시에 완료 카운트를 늘려도 안전하게 처리.
    private int totalCount = 0; // 전체 데이터 수 (250만 건 등).

    // JVM에서 운영체제의 CPU 사용률을 가져올 수 있는 객체.
    // osBean.getProcessCpuLoad() = JVM CPU 사용률 (%)
    // osBean.getSystemCpuLoad() = 전체 시스템 CPU 사용률 (%)
    private final OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    public void startAsyncUpdate() {
        SystemUtil.printStatus("Spring 기동 직후");

        Instant start = Instant.now();
        try {
            // 1?? 전체 행 수 세기
            totalCount = (int) Files.lines(FILE_PATH).count();
            log.info("총 {}건의 데이터 확인됨", totalCount);
            SystemUtil.printStatus("파일 건수 계산 완료");

            // 2?? BufferedReader로 한 줄씩 읽어서 처리
            try (BufferedReader reader = Files.newBufferedReader(FILE_PATH)) {
                List<String> buffer = new ArrayList<>(CHUNK_SIZE);
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.add(line.trim());
                    if (buffer.size() == CHUNK_SIZE) {
                        submitChunk(new ArrayList<>(buffer)); // 복사본 넘김
                        buffer.clear();
                    }
                }

                if (!buffer.isEmpty()) submitChunk(new ArrayList<>(buffer));
            }

            SystemUtil.printStatus("전체 비동기 작업 등록 완료");
            startMonitoringThread();

        } catch (IOException e) {
            log.error("파일 읽기 실패: {}", FILE_PATH, e);
        }

        Instant end = Instant.now();
        log.info("전체 청크 등록 완료 ({}초)", Duration.between(start, end).toSeconds());
    }


//    public void startAsyncUpdate() {
//        SystemUtil.printStatus("Spring 기동 직후");
//        try {
//
//            // 전체 비동기 등록 작업의 시작 시점 기록
//            Instant start = Instant.now();
//
//
//            // 1단계: 전체 행 수 세기 (메모리 사용 거의 없음)
//            try (Stream<String> lineStream = Files.lines(FILE_PATH)) {
//                totalCount = (int) lineStream.count();
//            }
//            log.info("총 {}건의 데이터 확인됨", totalCount);
//            SystemUtil.printStatus("파일 데이터 건수 계산 완료");
//
//
////            List<String> allLines = Files.readAllLines(Paths.get(FILE_PATH));
////            totalCount = allLines.size();
////            log.info("총 {}건의 데이터 읽음", totalCount);
////            250만개 기준 145MB 사용, 소요시간 0.2초
//
//            // 2단계: Stream 으로 파일 읽어서 CHUNK_SIZE 단위로 처리
//            try (BufferedReader reader = Files.newBufferedReader(FILE_PATH)) {
//                List<String> buffer = new ArrayList<>(CHUNK_SIZE);
//                String line;
//
//                while ((line = reader.readLine()) != null) {
//                    buffer.add(line.trim());
//                    if (buffer.size() == CHUNK_SIZE) {
//                        submitChunk(new ArrayList<>(buffer)); // ? 복사본 넘기기
//                        buffer.clear();
//                    }
//                }
//
//                if (!buffer.isEmpty()) submitChunk(new ArrayList<>(buffer));
//            }
//
//            // 별도 스레드로 5초마다 진행률을 찍는 모니터링 쓰레드 시작.
//            startMonitoringThread();
//
//
//
//            // 동시에 여러 DB 업데이트가 병렬로 수행
////            for (int i = 0; i < totalCount; i += CHUNK_SIZE) {
////                int end = Math.min(i + CHUNK_SIZE, totalCount);
////
////                // 원본(allLines)를 복사하지 않고, 원본 리스트의 내부 배열을 참조만 함
//////                List<String> chunk = allLines.subList(i, end);
////                List<String> chunk = null;
////
////                // GC 가 회수할수있도록(메모리 누수를 방지) 복사본 생성해서 복사본을 넘긴다.
////                updateChunkAsync(new ArrayList<>(chunk));
////            }
//
//            Instant end = Instant.now();
//            SystemUtil.printStatus("전체 비동기 작업 등록 완료");
//            log.info("전체 청크 작업 등록 완료 ({}초)", Duration.between(start, end).toSeconds());
//
//        } catch (IOException e) {
//            log.error("파일 읽기 실패: {}", FILE_PATH, e);
//        }
//    }

    // 비동기 처리 - AsyncConfig에서 정의한 "updateExecutor"를 사용
    @Async("updateExecutor") // 청크 단위를 병렬로 업데이트하는 비동기 처리
    public CompletableFuture<Void> updateChunkAsync(List<String> pkList) {
        try {
            migrationService.updateChunk(pkList);
            int done = processedCount.addAndGet(pkList.size());
            log.debug("청크 완료: {} / {}", done, totalCount);
        } catch (Exception e) {
            log.error("업데이트 실패", e);
        }
        return CompletableFuture.completedFuture(null);
    }

    /** 청크를 비동기 큐로 등록 */
    private void submitChunk(List<String> chunk) {
        // ? 복사본으로 넘겨 메모리 참조 끊기 (GC가 회수 가능)
        updateChunkAsync(new ArrayList<>(chunk));
    }

    private void startMonitoringThread() {
        Thread monitor = new Thread(() -> {
            long prev = 0;
            Instant prevTime = Instant.now();

            while (processedCount.get() < totalCount) {
                try {
                    Thread.sleep(LOG_INTERVAL_SEC * 1000L);
                    long now = processedCount.get();
                    Instant nowTime = Instant.now();

                    long diff = now - prev;
                    double seconds = Duration.between(prevTime, nowTime).toMillis() / 1000.0;
                    double rate = diff / seconds;
                    double jvmCpu = osBean.getProcessCpuLoad() * 100;
                    double sysCpu = osBean.getSystemCpuLoad() * 100;
                    double progress = (now * 100.0) / totalCount;

                    log.info(String.format(
                            "진행률: %.2f%% (%d/%d), 처리속도: %.1f건/초, JVM CPU: %.1f%%, SYS CPU: %.1f%%",
                            progress, now, totalCount, rate, jvmCpu, sysCpu));

                    prev = now;
                    prevTime = nowTime;

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            log.info("? 전체 업데이트 완료! 총 {}건 처리.", processedCount.get());
        });
        monitor.setDaemon(true);
        monitor.start();
    }
}