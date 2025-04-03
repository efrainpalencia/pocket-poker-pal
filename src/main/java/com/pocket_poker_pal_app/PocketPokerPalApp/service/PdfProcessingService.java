package com.pocket_poker_pal_app.PocketPokerPalApp.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PdfProcessingService {

    public List<String> extractChunksByBoldTitles(MultipartFile file) throws IOException {
        List<String> chunks = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();

        Pattern ruleStartPattern = Pattern.compile("^(Rule\\s+\\d+|\\d+:)\\b.*");

        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            for (String line : text.split("\\r?\\n")) {
                Matcher matcher = ruleStartPattern.matcher(line.trim());
                if (matcher.find()) {
                    // Save the current chunk before starting a new one
                    if (currentChunk.length() > 0) {
                        chunks.add(currentChunk.toString().trim());
                        currentChunk.setLength(0);
                    }
                }
                currentChunk.append(line).append("\n");
            }

            if (currentChunk.length() > 0) {
                chunks.add(currentChunk.toString().trim());
            }
        }

        return chunks;
    }

    public List<String> extractChunks(MultipartFile file) throws IOException {
        List<String> chunks = new ArrayList<>();

        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String fullText = pdfStripper.getText(document);

            // Basic chunking by rule (example split)
            String[] splitByRules = fullText.split("(?=\\d{1,3}:\\s)");

            for (String splitByRule : splitByRules) {
                String chunk = splitByRule.trim();
                if (!chunk.isEmpty()) {
                    chunks.add(chunk);
                }
            }
        }

        return chunks;
    }
}
