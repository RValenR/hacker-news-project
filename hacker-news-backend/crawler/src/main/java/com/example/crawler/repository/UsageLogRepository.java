package com.example.crawler.repository;

import com.example.crawler.model.UsageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort;
import java.util.List;

@Repository
public interface UsageLogRepository extends JpaRepository<UsageLog, Long> {

    // Obtener logs ordenados por timestamp descendente
    List<UsageLog> findAll(Sort sort);
}