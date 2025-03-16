package com.pocket_poker_pal_app.PocketPokerPalApp.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfProcessingService {

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

