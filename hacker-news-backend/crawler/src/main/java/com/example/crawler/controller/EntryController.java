package com.example.crawler.controller;

import com.example.crawler.dto.EntryDTO;
import com.example.crawler.model.Entry;
import com.example.crawler.model.UsageLog;
import com.example.crawler.repository.EntryRepository;
import com.example.crawler.service.HackerNewsScraperService;
import com.example.crawler.service.UsageLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entries")
@CrossOrigin(origins = "http://localhost:4200")

public class EntryController {

    @GetMapping
    public ResponseEntity<List<EntryDTO>> getEntries() {
        return ResponseEntity.ok(null);
    }

    @PostMapping("/refresh")
    public ResponseEntity<List<EntryDTO>> refreshEntries() {
        return ResponseEntity.ok(null);
    }

    @GetMapping("/filter/more-than-5")
    public ResponseEntity<List<EntryDTO>> filterMoreThan5Words() {
        return ResponseEntity.ok(null);
    }

    @GetMapping("/filter/less-equal-5")
    public ResponseEntity<List<EntryDTO>> filterLessEqual5Words() {
        return ResponseEntity.ok(null);
    }

    @GetMapping("/usage-logs")
    public ResponseEntity<List<UsageLog>> getUsageLogs() {
        return ResponseEntity.ok(null);
    }

    private EntryDTO convertToDTO(Entry entry) {
        return new EntryDTO(
                entry.getId(),
                entry.getPosition(),
                entry.getTitle(),
                entry.getPoints(),
                entry.getComments(),
                entry.getWordCount()
        );
    }
}
