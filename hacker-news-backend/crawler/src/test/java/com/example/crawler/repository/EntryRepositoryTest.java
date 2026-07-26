package com.example.crawler.repository;

import com.example.crawler.model.Entry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class EntryRepositoryTest {

    @Autowired
    private EntryRepository entryRepository;

    private Entry entry1;
    private Entry entry2;
    private Entry entry3;

    @BeforeEach
    void setUp() {
        // Limpiar datos previos
        entryRepository.deleteAll();

        entry1 = new Entry(1, "Short title", 100, 50, 2);
        entry2 = new Entry(2, "This is a longer title with more words", 200, 30, 7);
        entry3 = new Entry(3, "Medium title here", 150, 40, 3);

        entryRepository.save(entry1);
        entryRepository.save(entry2);
        entryRepository.save(entry3);
    }

    @Test
    void shouldFindAllEntries() {
        List<Entry> entries = entryRepository.findAll();
        assertThat(entries).hasSize(3);
    }

    @Test
    void shouldFindByWordCountGreaterThan() {
        List<Entry> entries = entryRepository.findByWordCountGreaterThan(5);
        assertThat(entries).hasSize(1);
        assertThat(entries.get(0).getTitle()).contains("longer title");
        assertThat(entries.get(0).getWordCount()).isGreaterThan(5);
    }

    @Test
    void shouldFindByWordCountLessThanEqual() {
        List<Entry> entries = entryRepository.findByWordCountLessThanEqual(3);
        assertThat(entries).hasSize(2);
        assertThat(entries).extracting(Entry::getTitle)
                .containsExactlyInAnyOrder("Short title", "Medium title here");
        assertThat(entries).allMatch(e -> e.getWordCount() <= 3);
    }

    @Test
    void shouldDeleteAllEntries() {
        entryRepository.deleteAll();
        List<Entry> entries = entryRepository.findAll();
        assertThat(entries).isEmpty();
    }

    @Test
    void shouldSaveEntry() {
        Entry newEntry = new Entry(4, "New entry title", 300, 100, 4);
        Entry saved = entryRepository.save(newEntry);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getPosition()).isEqualTo(4);
        assertThat(saved.getTitle()).isEqualTo("New entry title");
    }

    @Test
    void shouldUpdateEntry() {
        Entry entry = entryRepository.findAll().get(0);
        entry.setTitle("Updated title");
        entry.setPoints(200);

        Entry updated = entryRepository.save(entry);

        assertThat(updated.getTitle()).isEqualTo("Updated title");
        assertThat(updated.getPoints()).isEqualTo(200);
    }
}