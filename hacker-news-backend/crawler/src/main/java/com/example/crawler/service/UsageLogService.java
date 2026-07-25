package com.example.crawler.service;

import com.example.crawler.model.UsageLog;
import com.example.crawler.repository.UsageLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import java.util.List;

@Service
public class UsageLogService {

    @Autowired
    private UsageLogRepository usageLogRepository;

    public void saveLog(String filterType, int resultCount, long responseTimeMs, String endpoint) {
        UsageLog log = new UsageLog(filterType, resultCount, responseTimeMs, endpoint);
        usageLogRepository.save(log);
    }

    public List<UsageLog> getLastLogs(int limit) {
        Sort sort = Sort.by(Sort.Direction.DESC, "timestamp");
        List<UsageLog> allLogs = usageLogRepository.findAll(sort);
        return allLogs.stream().limit(limit).toList();
    }

    public List<UsageLog> getAllLogs() {
        return usageLogRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
    }
}