package com.pocket_poker_pal_app.PocketPokerPalApp.service;


import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenAIEmbeddingService {

    private final EmbeddingClient embeddingClient;

    /**
     * Generates an embedding vector for a given text chunk.
     * @param text The text content to embed.
     * @return List<Double> representing the embedding vector.
     */
    public List<Double> generateEmbedding(String text) {
        return embeddingClient.embed(text);
    }
}
