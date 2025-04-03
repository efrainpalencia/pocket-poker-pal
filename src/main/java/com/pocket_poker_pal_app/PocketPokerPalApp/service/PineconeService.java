package com.pocket_poker_pal_app.PocketPokerPalApp.service;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PineconeService {

    private static final MediaType JSON = MediaType.parse("application/json");

    private final OkHttpClient client;

    private final String pineconeApiKey;
    private final String pineconeIndexUrl;

    @Autowired
    public PineconeService(
            @Value("${pinecone.api.key}") String pineconeApiKey,
            @Value("${pinecone.index.url}") String pineconeIndexUrl
    ) {
        this.client = new OkHttpClient();
        this.pineconeApiKey = pineconeApiKey;
        this.pineconeIndexUrl = pineconeIndexUrl;
    }

    // ✅ Constructor for testing
    public PineconeService(OkHttpClient client, String pineconeApiKey, String pineconeIndexUrl) {
        this.client = client;
        this.pineconeApiKey = pineconeApiKey;
        this.pineconeIndexUrl = pineconeIndexUrl;
    }

    public void upsertVector(String id, List<Double> vector, Map<String, Object> metadata) throws IOException {
        JSONObject vectorObj = new JSONObject();
        vectorObj.put("id", id);

        // ✅ Wrap the vector List in a JSONArray (safe for any org.json version)
        vectorObj.put("values", new JSONArray(vector));

        vectorObj.put("metadata", new JSONObject(metadata));

        JSONArray vectorsArray = new JSONArray();
        vectorsArray.put(vectorObj);

        JSONObject requestBody = new JSONObject();
        requestBody.put("vectors", vectorsArray);

        Request request = new Request.Builder()
                .url(pineconeIndexUrl + "/vectors/upsert")
                .post(RequestBody.create(requestBody.toString(), JSON))
                .addHeader("Api-Key", pineconeApiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();

            if (!response.isSuccessful() || responseBody == null) {
                String errorMsg = (responseBody != null)
                        ? responseBody.string()
                        : "Unknown error, empty response body";

                throw new IOException("Failed to upsert vector: " + errorMsg);
            }
        }
    }

    // ✅ Query relevant chunks from Pinecone index
    public List<String> queryRelevantChunks(List<Double> embedding) throws IOException {
        JSONObject requestBody = new JSONObject();
        requestBody.put("vector", new JSONArray(embedding));
        requestBody.put("topK", 5);
        requestBody.put("includeMetadata", true);

        Request request = new Request.Builder()
                .url(pineconeIndexUrl + "/query")
                .post(RequestBody.create(requestBody.toString(), JSON))
                .addHeader("Api-Key", pineconeApiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Query failed: " + response.message());
            }

            JSONObject result = new JSONObject(response.body().string());
            JSONArray matches = result.getJSONArray("matches");

            List<String> chunks = new ArrayList<>();
            for (int i = 0; i < matches.length(); i++) {
                JSONObject metadata = matches.getJSONObject(i).getJSONObject("metadata");
                chunks.add(metadata.getString("text"));
            }

            return chunks;
        }
    }

}
