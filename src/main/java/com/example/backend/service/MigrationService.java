package com.example.backend.service;

import com.example.backend.repository.MigrationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class MigrationService {
    private final MigrationRepository migrationRepository;

    @Transactional
    public void updateChunk(List<String> pkList) {
        migrationRepository.bulkUpdateByPkList(pkList);
    }

}
