package com.pocket_poker_pal_app.PocketPokerPalApp.service;

import com.pocket_poker_pal_app.PocketPokerPalApp.entity.RulebookEntity;
import com.pocket_poker_pal_app.PocketPokerPalApp.exception.EmptyRulebookException;
import com.pocket_poker_pal_app.PocketPokerPalApp.repository.RulebookRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RulebookVectorUploadService {

    private final PdfProcessingService pdfProcessingService;
    private final OpenAIEmbeddingService openAIEmbeddingService;
    private final PineconeService pineconeService;
    private final RulebookRepository rulebookRepository;

    @Transactional
    public void processAndUpload(MultipartFile pdfFile, RulebookEntity rulebookEntity) throws IOException {
        List<String> chunks = pdfProcessingService.extractChunksByBoldTitles(pdfFile);


        if (chunks.isEmpty()) {
            throw new EmptyRulebookException("PDF parsing resulted in no chunks. Rulebook cannot be processed.");
        }

        for (int i = 0; i < chunks.size(); i++) {
            String chunkText = chunks.get(i).trim();

            if (chunkText.isEmpty()) continue;

            // Clean and truncate
            String safeChunk = chunkText.length() > 8000
                    ? chunkText.substring(0, 8000)
                    : chunkText.replaceAll("[^\\x00-\\x7F]", "");

            try {
                System.out.println("🔍 Embedding chunk [" + i + "] (length: " + safeChunk.length() + ")");
                List<Double> embedding = openAIEmbeddingService.generateEmbedding(safeChunk);

                Map<String, Object> metadata = new HashMap<>();
                metadata.put("source", rulebookEntity.getSource().name());
                metadata.put("rulebook", rulebookEntity.getTitle());
                metadata.put("rule_version", rulebookEntity.getVersion());
                metadata.put("chunk_index", i);
                metadata.put("text", safeChunk);

                 pineconeService.upsertVector(rulebookEntity.getId().toString() + "-chunk-" + i, embedding, metadata);

            } catch (Exception e) {
                System.err.println("❌ Failed to embed chunk [" + i + "]: " + e.getMessage());
            }
        }
 // Print debugger
//        chunks.forEach(chunk -> {
//            System.out.println("\n\n==== New Chunk ====\n" + chunk);
//        });


        rulebookRepository.save(rulebookEntity);
    }
}

