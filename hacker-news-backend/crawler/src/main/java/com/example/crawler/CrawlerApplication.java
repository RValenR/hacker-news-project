package com.example.crawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CrawlerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrawlerApplication.class, args);
		System.out.println("🚀 Hacker News Crawler API started on http://localhost:8080");
		System.out.println("📊 H2 Console: http://localhost:8080/h2-console");
		System.out.println("📡 API Endpoints: http://localhost:8080/api/entries");
	}

}
