package com.pocket_poker_pal_app.PocketPokerPalApp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class WhisperService {

    @Value("${openai.api.key}")
    private String openaiApiKey;

    private final OkHttpClient client = new OkHttpClient();

    public String transcribe(MultipartFile audioFile) throws IOException {
        // Get content type from the uploaded file
        String contentType = audioFile.getContentType();
        String fileName = audioFile.getOriginalFilename();

        // Fallback if contentType is null or empty
        if (contentType == null || contentType.isBlank()) {
            contentType = "audio/m4a"; // default fallback
        }

        MediaType mediaType = MediaType.parse(contentType);
        RequestBody fileBody = RequestBody.create(audioFile.getBytes(), mediaType);

        // Debug logs
        System.out.println("Transcribing file: " + fileName);
        System.out.println("Detected content type: " + contentType);

        MultipartBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName, fileBody)
                .addFormDataPart("model", "whisper-1")
                .build();

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/audio/transcriptions")
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + openaiApiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("Whisper API Error Code: " + response.code());
                System.err.println("Whisper API Error Body: " + response.body().string());
                throw new IOException("Whisper API failed.");
            }

            String responseJson = response.body().string();
            return new ObjectMapper()
                    .readTree(responseJson)
                    .get("text").asText();
        }
    }
}
