package com.example.crawler.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "usage_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class UsageLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String filterType;

    @Column(nullable = false)
    private int resultCount;

    @Column(nullable = false)
    private long responseTimeMs;

    @Column
    private String endpoint;

    // Constructor simplificado
    public UsageLog(String filterType, int resultCount, long responseTimeMs, String endpoint) {
        this.timestamp = LocalDateTime.now();
        this.filterType = filterType;
        this.resultCount = resultCount;
        this.responseTimeMs = responseTimeMs;
        this.endpoint = endpoint;
    }
}
