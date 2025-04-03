package com.pocket_poker_pal_app.PocketPokerPalApp.service;

import com.pocket_poker_pal_app.PocketPokerPalApp.entity.RulebookEntity;
import com.pocket_poker_pal_app.PocketPokerPalApp.exception.EmptyRulebookException;
import com.pocket_poker_pal_app.PocketPokerPalApp.repository.RulebookRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RulebookVectorUploadService {

    private final PdfProcessingService pdfProcessingService;
    private final OpenAIEmbeddingService openAIEmbeddingService;
    private final PineconeService pineconeService;
    private final RulebookRepository rulebookRepository;

    private static final int MAX_CHUNK_LENGTH = 2000; // Characters, not tokens

    @Transactional
    public void processAndUpload(MultipartFile pdfFile, RulebookEntity rulebookEntity) throws IOException {
        List<String> chunks = pdfProcessingService.extractChunksByBoldTitles(pdfFile);

        if (chunks.isEmpty()) {
            throw new EmptyRulebookException("PDF parsing resulted in no chunks. Rulebook cannot be processed.");
        }

        int embeddingIndex = 0;

        for (String chunkText : chunks) {
            if (chunkText == null || chunkText.trim().isEmpty()) continue;

            // Split large chunks
            List<String> subChunks = splitChunkByLength(chunkText.trim(), MAX_CHUNK_LENGTH);

            for (String subChunk : subChunks) {
                try {
                    System.out.println("🔍 Embedding chunk [" + embeddingIndex + "] (length: " + subChunk.length() + ")");
                    List<Double> embedding = openAIEmbeddingService.generateEmbedding(subChunk);

                    Map<String, Object> metadata = new HashMap<>();
                    metadata.put("source", rulebookEntity.getSource().name());
                    metadata.put("rulebook", rulebookEntity.getTitle());
                    metadata.put("rule_version", rulebookEntity.getVersion());
                    metadata.put("chunk_index", embeddingIndex);
                    metadata.put("text", subChunk);

                    pineconeService.upsertVector(rulebookEntity.getId().toString() + "-chunk-" + embeddingIndex, embedding, metadata);

                    embeddingIndex++;
                } catch (Exception e) {
                    System.err.println("❌ Failed to embed chunk [" + embeddingIndex + "]: " + e.getMessage());
                }
            }
        }

        // Print debugger
        chunks.forEach(chunk -> {
            System.out.println("\n\n==== New Chunk ====\n" + chunk);
        });

        rulebookRepository.save(rulebookEntity);
    }

    /**
     * Splits a large chunk into smaller chunks based on max character length.
     * Tries to split on newlines where possible.
     */
    private List<String> splitChunkByLength(String text, int maxLength) {
        List<String> result = new ArrayList<>();
        int start = 0;

        while (start < text.length()) {
            int end = Math.min(start + maxLength, text.length());

            // Try to split at the last newline before end
            int lastNewline = text.lastIndexOf("\n", end);
            if (lastNewline > start && lastNewline < end) {
                end = lastNewline;
            }

            result.add(text.substring(start, end).trim());
            start = end;
        }

        return result;
    }
}
