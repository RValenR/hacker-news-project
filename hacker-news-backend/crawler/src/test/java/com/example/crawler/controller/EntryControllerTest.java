package com.example.crawler.controller;

import com.example.crawler.dto.EntryDTO;
import com.example.crawler.model.Entry;
import com.example.crawler.repository.EntryRepository;
import com.example.crawler.service.HackerNewsScraperService;
import com.example.crawler.service.UsageLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntryControllerTest {

    @Mock
    private HackerNewsScraperService scraperService;

    @Mock
    private EntryRepository entryRepository;

    @Mock
    private UsageLogService usageLogService;

    @InjectMocks
    private EntryController entryController;

    private List<Entry> mockEntries;

    @BeforeEach
    void setUp() {
        mockEntries = Arrays.asList(
                new Entry(1, "Test Entry 1", 100, 50, 3),
                new Entry(2, "Test Entry with more words", 200, 30, 6)
        );
    }

    @Test
    void shouldGetEntriesFromDatabase() {
        when(entryRepository.findAll()).thenReturn(mockEntries);

        ResponseEntity<List<EntryDTO>> response = entryController.getEntries();

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getTitle()).isEqualTo("Test Entry 1");

        verify(entryRepository, times(1)).findAll();
        verify(scraperService, never()).scrapeTopEntries();
        verify(usageLogService, times(1)).saveLog(anyString(), anyInt(), anyLong(), anyString());
    }

    @Test
    void shouldScrapeWhenDatabaseEmpty() {
        when(entryRepository.findAll()).thenReturn(Arrays.asList());
        when(scraperService.scrapeTopEntries()).thenReturn(mockEntries);

        ResponseEntity<List<EntryDTO>> response = entryController.getEntries();

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).hasSize(2);

        verify(entryRepository, times(1)).findAll();
        verify(scraperService, times(1)).scrapeTopEntries();
        verify(entryRepository, times(1)).saveAll(anyList());
    }

    @Test
    void shouldRefreshEntries() {
        when(scraperService.scrapeTopEntries()).thenReturn(mockEntries);
        doNothing().when(entryRepository).deleteAll();

        ResponseEntity<List<EntryDTO>> response = entryController.refreshEntries();

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).hasSize(2);

        verify(entryRepository, times(1)).deleteAll();
        verify(scraperService, times(1)).scrapeTopEntries();
        verify(entryRepository, times(1)).saveAll(anyList());
        verify(usageLogService, times(1)).saveLog(eq("REFRESH"), anyInt(), anyLong(), anyString());
    }

    @Test
    void shouldFilterMoreThan5Words() {
        when(entryRepository.findAll()).thenReturn(mockEntries);

        ResponseEntity<List<EntryDTO>> response = entryController.filterMoreThan5Words();

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getWordCount()).isGreaterThan(5);
        assertThat(response.getBody().get(0).getTitle()).isEqualTo("Test Entry with more words");

        verify(usageLogService, times(1)).saveLog(eq("MORE_THAN_5"), anyInt(), anyLong(), anyString());
    }

    @Test
    void shouldFilterLessEqual5Words() {
        when(entryRepository.findAll()).thenReturn(mockEntries);

        ResponseEntity<List<EntryDTO>> response = entryController.filterLessEqual5Words();

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getWordCount()).isLessThanOrEqualTo(5);
        assertThat(response.getBody().get(0).getTitle()).isEqualTo("Test Entry 1");

        verify(usageLogService, times(1)).saveLog(eq("LESS_EQUAL_5"), anyInt(), anyLong(), anyString());
    }

    @Test
    void shouldReturnSortedByCommentsForMoreThan5() {
        List<Entry> entries = Arrays.asList(
                new Entry(1, "First entry with many words", 100, 10, 6),
                new Entry(2, "Second entry with lots of words here", 200, 50, 7),
                new Entry(3, "Third entry with even more words", 150, 30, 8)
        );

        when(entryRepository.findAll()).thenReturn(entries);

        ResponseEntity<List<EntryDTO>> response = entryController.filterMoreThan5Words();

        List<EntryDTO> result = response.getBody();
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getComments()).isEqualTo(50);
        assertThat(result.get(1).getComments()).isEqualTo(30);
        assertThat(result.get(2).getComments()).isEqualTo(10);
    }

    @Test
    void shouldReturnSortedByPointsForLessEqual5() {
        List<Entry> entries = Arrays.asList(
                new Entry(1, "First", 100, 10, 3),
                new Entry(2, "Second", 200, 50, 4),
                new Entry(3, "Third", 150, 30, 5)
        );

        when(entryRepository.findAll()).thenReturn(entries);

        ResponseEntity<List<EntryDTO>> response = entryController.filterLessEqual5Words();

        List<EntryDTO> result = response.getBody();
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getPoints()).isEqualTo(200);
        assertThat(result.get(1).getPoints()).isEqualTo(150);
        assertThat(result.get(2).getPoints()).isEqualTo(100);
    }
}