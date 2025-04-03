package com.pocket_poker_pal_app.PocketPokerPalApp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RulebookSearchService {

    private final OpenAIEmbeddingService openAIEmbeddingService;

    @Value("${pinecone.index.url}")
    private String pineconeIndexUrl;

    @Value("${pinecone.api.key}")
    private String pineconeApiKey;

    private static final MediaType JSON = MediaType.parse("application/json");

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Returns the top matching rulebook chunks for a given question.
     *
     * @param question User input question
     * @param topK     Number of top chunks to retrieve
     * @return A list of matched rulebook chunks
     */
    public List<String> searchRelevantChunks(String question, int topK) throws IOException {
        List<Double> embedding = openAIEmbeddingService.generateEmbedding(question);

        Map<String, Object> vector = new HashMap<>();
        vector.put("vector", embedding);
        vector.put("topK", topK);
        vector.put("includeMetadata", true);

        String requestBody = objectMapper.writeValueAsString(vector);

        Request request = new Request.Builder()
                .url(pineconeIndexUrl + "/query")
                .post(RequestBody.create(requestBody, JSON))
                .addHeader("Api-Key", pineconeApiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new IOException("Failed to query Pinecone: " + (response.body() != null ? response.body().string() : "No response body"));
            }

            JsonNode root = objectMapper.readTree(response.body().string());
            JsonNode matches = root.path("matches");

            return extractTextChunks(matches);
        }
    }

    private List<String> extractTextChunks(JsonNode matches) {
        List<String> chunks = new ArrayList<>();
        for (JsonNode match : matches) {
            JsonNode metadata = match.path("metadata");
            if (metadata.has("text")) {
                chunks.add(metadata.get("text").asText());
            }
        }
        return chunks;
    }
}
