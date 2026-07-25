package com.example.crawler.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntryDTO {
    private Long id;
    private int position;
    private String title;
    private int points;
    private int comments;
    private int wordCount;
}