package com.example.crawler.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "entries")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Entry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int position;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false)
    private int points;

    @Column(nullable = false)
    private int comments;

    @Column(nullable = false)
    private int wordCount;

    // Constructor para crear Entry sin ID
    public Entry(int position, String title, int points, int comments, int wordCount) {
        this.position = position;
        this.title = title;
        this.points = points;
        this.comments = comments;
        this.wordCount = wordCount;
    }
}
