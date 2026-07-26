package com.example.crawler.repository;

import com.example.crawler.model.UsageLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UsageLogRepositoryTest {

    @Autowired
    private UsageLogRepository usageLogRepository;

    private UsageLog log1;
    private UsageLog log2;
    private UsageLog log3;

    @BeforeEach
    void setUp() {
        // Limpiar datos previos
        usageLogRepository.deleteAll();

        log1 = new UsageLog("MORE_THAN_5", 12, 245, "/api/entries/filter/more-than-5");
        log2 = new UsageLog("LESS_EQUAL_5", 18, 189, "/api/entries/filter/less-equal-5");
        log3 = new UsageLog("ALL", 30, 320, "/api/entries");

        // Configurar timestamps manualmente usando reflexión
        try {
            java.lang.reflect.Field timestampField = UsageLog.class.getDeclaredField("timestamp");
            timestampField.setAccessible(true);
            timestampField.set(log1, LocalDateTime.now().minusDays(2));
            timestampField.set(log2, LocalDateTime.now().minusDays(1));
            timestampField.set(log3, LocalDateTime.now());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        usageLogRepository.save(log1);
        usageLogRepository.save(log2);
        usageLogRepository.save(log3);
    }

    @Test
    void shouldFindAllSortedByTimestampDesc() {
        Sort sort = Sort.by(Sort.Direction.DESC, "timestamp");
        List<UsageLog> logs = usageLogRepository.findAll(sort);

        assertThat(logs).hasSize(3);
        assertThat(logs.get(0).getFilterType()).isEqualTo("ALL");
        assertThat(logs.get(1).getFilterType()).isEqualTo("LESS_EQUAL_5");
        assertThat(logs.get(2).getFilterType()).isEqualTo("MORE_THAN_5");
    }

    @Test
    void shouldFindAllSortedByTimestampAsc() {
        Sort sort = Sort.by(Sort.Direction.ASC, "timestamp");
        List<UsageLog> logs = usageLogRepository.findAll(sort);

        assertThat(logs).hasSize(3);
        assertThat(logs.get(0).getFilterType()).isEqualTo("MORE_THAN_5");
        assertThat(logs.get(1).getFilterType()).isEqualTo("LESS_EQUAL_5");
        assertThat(logs.get(2).getFilterType()).isEqualTo("ALL");
    }

    @Test
    void shouldSaveLog() {
        UsageLog newLog = new UsageLog("REFRESH", 25, 150, "/api/entries/refresh");
        UsageLog saved = usageLogRepository.save(newLog);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFilterType()).isEqualTo("REFRESH");
        assertThat(saved.getResultCount()).isEqualTo(25);
        assertThat(saved.getTimestamp()).isNotNull();
    }

    @Test
    void shouldFindAllLogs() {
        List<UsageLog> logs = usageLogRepository.findAll();
        assertThat(logs).hasSize(3);
    }
}