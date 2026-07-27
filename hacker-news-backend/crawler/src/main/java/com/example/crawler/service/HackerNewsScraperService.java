package com.example.crawler.service;

import com.example.crawler.model.Entry;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class HackerNewsScraperService {

    private static final Logger logger = LoggerFactory.getLogger(HackerNewsScraperService.class);
    private static final String HACKER_NEWS_URL = "https://news.ycombinator.com/";
    private static final Pattern SYMBOL_PATTERN = Pattern.compile("[^a-zA-Z0-9\\s]");

    /**
     * Extrae las primeras 30 entradas de Hacker News
     * Get the first 30 inputs of hacker news
     */
    public List<Entry> scrapeTopEntries() {
        List<Entry> entries = new ArrayList<>();

        try {
            logger.info("Iniciando scraping de Hacker News...");
            logger.info("Start scraping...");
            Document doc = Jsoup.connect(HACKER_NEWS_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();

            // Seleccionar las filas de la tabla
            Elements rows = doc.select("tr.athing");
            int count = 0;

            for (Element row : rows) {
                if (count >= 30) break; // Justo the first 30

                try {
                    // Extraer posición
                    // Get position
                    Element rankElement = row.selectFirst(".rank");
                    int position = Integer.parseInt(rankElement.text().replace(".", ""));

                    // Extraer título
                    // Get title
                    Element titleElement = row.selectFirst(".titleline > a");
                    String title = titleElement != null ? titleElement.text() : "No title";

                    // Extraer puntos y comentarios (están en la siguiente fila)
                    // get points and comment
                    Element subtext = row.nextElementSibling();
                    if (subtext != null) {
                        Elements scoreElements = subtext.select(".score");
                        int points = scoreElements.isEmpty() ? 0 :
                                Integer.parseInt(scoreElements.first().text().replace(" points", ""));

                        Elements commentElements = subtext.select("a:contains(comment)");
                        int comments = 0;
                        if (!commentElements.isEmpty()) {
                            String commentText = commentElements.first().text();
                            comments = Integer.parseInt(commentText.replace(" comments", "").replace(" comment", ""));
                        }

                        // Contar palabras del título (excluyendo símbolos)
                        // Count words method
                        int wordCount = countWords(title);

                        Entry entry = new Entry(position, title, points, comments, wordCount);
                        entries.add(entry);
                        logger.debug("Entrada extraída: #{} - {}", position, title);
                    }

                    count++;

                } catch (Exception e) {
                    logger.warn("Error procesando entrada {}: {}", count + 1, e.getMessage());
                }
            }

            logger.info("Scraping completado. {} entradas extraídas.", entries.size());

        } catch (IOException e) {
            logger.error("Error al conectar con Hacker News: {}", e.getMessage());
            throw new RuntimeException("Error al hacer scraping: " + e.getMessage(), e);
        }

        return entries;
    }

    /**
     * Cuenta las palabras en un título, excluyendo símbolos
     * Count words in the title
     */
    private int countWords(String title) {
        if (title == null || title.isEmpty()) {
            return 0;
        }

        // Eliminar símbolos pero mantener letras, números y espacios
        // deletes simbols
        String cleaned = SYMBOL_PATTERN.matcher(title).replaceAll(" ");
        // Dividir por espacios y filtrar palabras vacías
        // filter empty words
        String[] words = cleaned.trim().split("\\s+");
        return words.length;
    }
}