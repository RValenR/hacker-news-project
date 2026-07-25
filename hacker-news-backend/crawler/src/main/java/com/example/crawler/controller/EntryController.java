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

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/entries")
@CrossOrigin(origins = "http://localhost:4200")
public class EntryController {

    @Autowired
    private HackerNewsScraperService scraperService;

    @Autowired
    private EntryRepository entryRepository;

    @Autowired
    private UsageLogService usageLogService;

    /**
     * Obtener todas las entradas (realiza scraping si no hay datos)
     */
    @GetMapping
    public ResponseEntity<List<EntryDTO>> getEntries() {
        long startTime = System.currentTimeMillis();

        List<Entry> entries = entryRepository.findAll();

        // Si no hay datos, hacer scraping
        if (entries.isEmpty()) {
            entries = scraperService.scrapeTopEntries();
            entryRepository.saveAll(entries);
        }

        List<EntryDTO> dtos = entries.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        usageLogService.saveLog("ALL", dtos.size(), endTime - startTime, "/api/entries");

        return ResponseEntity.ok(dtos);
    }

    /**
     * Refrescar datos (hacer scraping nuevamente)
     */
    @PostMapping("/refresh")
    public ResponseEntity<List<EntryDTO>> refreshEntries() {
        long startTime = System.currentTimeMillis();

        // Eliminar datos antiguos
        entryRepository.deleteAll();

        // Hacer scraping
        List<Entry> entries = scraperService.scrapeTopEntries();
        entryRepository.saveAll(entries);

        List<EntryDTO> dtos = entries.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        usageLogService.saveLog("REFRESH", dtos.size(), endTime - startTime, "/api/entries/refresh");

        return ResponseEntity.ok(dtos);
    }

    /**
     * Filtrar entradas con más de 5 palabras, ordenadas por comentarios
     */
    @GetMapping("/filter/more-than-5")
    public ResponseEntity<List<EntryDTO>> filterMoreThan5Words() {
        long startTime = System.currentTimeMillis();

        List<Entry> entries = entryRepository.findAll();

        // Si no hay datos, hacer scraping primero
        if (entries.isEmpty()) {
            entries = scraperService.scrapeTopEntries();
            entryRepository.saveAll(entries);
        }

        // Filtrar: más de 5 palabras, ordenar por comentarios descendente
        List<Entry> filtered = entries.stream()
                .filter(e -> e.getWordCount() > 5)
                .sorted(Comparator.comparingInt(Entry::getComments).reversed())
                .collect(Collectors.toList());

        List<EntryDTO> dtos = filtered.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        usageLogService.saveLog("MORE_THAN_5", dtos.size(), endTime - startTime, "/api/entries/filter/more-than-5");

        return ResponseEntity.ok(dtos);
    }

    /**
     * Filtrar entradas con ≤ 5 palabras, ordenadas por puntos
     */
    @GetMapping("/filter/less-equal-5")
    public ResponseEntity<List<EntryDTO>> filterLessEqual5Words() {
        long startTime = System.currentTimeMillis();

        List<Entry> entries = entryRepository.findAll();

        // Si no hay datos, hacer scraping primero
        if (entries.isEmpty()) {
            entries = scraperService.scrapeTopEntries();
            entryRepository.saveAll(entries);
        }

        // Filtrar: ≤ 5 palabras, ordenar por puntos descendente
        List<Entry> filtered = entries.stream()
                .filter(e -> e.getWordCount() <= 5)
                .sorted(Comparator.comparingInt(Entry::getPoints).reversed())
                .collect(Collectors.toList());

        List<EntryDTO> dtos = filtered.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        long endTime = System.currentTimeMillis();
        usageLogService.saveLog("LESS_EQUAL_5", dtos.size(), endTime - startTime, "/api/entries/filter/less-equal-5");

        return ResponseEntity.ok(dtos);
    }

    /**
     * Obtener logs de uso
     */
    @GetMapping("/usage-logs")
    public ResponseEntity<List<UsageLog>> getUsageLogs() {
        return ResponseEntity.ok(usageLogService.getLastLogs(20));
    }

    /**
     * Convertir Entry a EntryDTO
     */
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