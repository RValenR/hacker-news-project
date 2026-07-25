package com.example.crawler.repository;

import com.example.crawler.model.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EntryRepository extends JpaRepository<Entry, Long> {

    // Eliminar todas las entradas (para refrescar)
    void deleteAll();

    // Buscar por número de palabras
    List<Entry> findByWordCountGreaterThan(int wordCount);

    List<Entry> findByWordCountLessThanEqual(int wordCount);
}