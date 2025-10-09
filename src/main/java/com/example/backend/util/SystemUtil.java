package com.example.backend.util;


import com.sun.management.OperatingSystemMXBean;
import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;

@Slf4j
public class SystemUtil {

    private static final OperatingSystemMXBean osBean =
            (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    /** 시스템 상태 출력 */
    public static void printStatus(String title) {
        Runtime rt = Runtime.getRuntime();

        double usedMem = (rt.totalMemory() - rt.freeMemory()) / (1024.0 * 1024.0);
        double totalMem = rt.totalMemory() / (1024.0 * 1024.0);
        double maxMem = rt.maxMemory() / (1024.0 * 1024.0);
        double cpuJvm = osBean.getProcessCpuLoad() * 100;
        double cpuSys = osBean.getSystemCpuLoad() * 100;

        log.info("===========================================");
        log.info(String.format("[%s]", title));
        log.info(String.format("JVM 사용 메모리: %.2f MB / 전체: %.2f MB (최대: %.2f MB)", usedMem, totalMem, maxMem));
        log.info(String.format("CPU 사용률: JVM %.1f%%, System %.1f%%", cpuJvm, cpuSys));
        log.info("===========================================");
    }
}
