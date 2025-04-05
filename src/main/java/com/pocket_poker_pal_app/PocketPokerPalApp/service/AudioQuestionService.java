package com.pocket_poker_pal_app.PocketPokerPalApp.service;

import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AudioQuestionService {

    private final RulebookSearchService rulebookSearchService;
    private final OpenAIAnswerService openAIAnswerService;

    @Value("${openai.api.key}")
    private String openaiApiKey;

    private static final MediaType MEDIA_TYPE_AUDIO = MediaType.parse("audio/mpeg");
    private final OkHttpClient client = new OkHttpClient();

    public String handleAudioQuestion(MultipartFile audioFile) throws IOException {
        // Step 1: Transcribe audio using OpenAI Whisper API
        String question = transcribeWithWhisper(audioFile);

        // Step 2: Search relevant chunks from Pinecone
        List<String> chunks = rulebookSearchService.searchRelevantChunks(question, 5);

        // Step 3: Get answer from OpenAI
        return openAIAnswerService.getAnswer(question, chunks);
    }

    private String transcribeWithWhisper(MultipartFile audioFile) throws IOException {
        RequestBody fileBody = RequestBody.create(audioFile.getBytes(), MEDIA_TYPE_AUDIO);

        MultipartBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", audioFile.getOriginalFilename(), fileBody)
                .addFormDataPart("model", "whisper-1")
                .build();

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/audio/transcriptions")
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + openaiApiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Whisper API failed: " + response.body().string());
            }

            String responseJson = response.body().string();
            return new com.fasterxml.jackson.databind.ObjectMapper()
                    .readTree(responseJson)
                    .get("text").asText();
        }
    }
}
