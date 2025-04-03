package com.pocket_poker_pal_app.PocketPokerPalApp.controller;

import com.pocket_poker_pal_app.PocketPokerPalApp.service.OpenAIAnswerService;
import com.pocket_poker_pal_app.PocketPokerPalApp.service.OpenAIEmbeddingService;
import com.pocket_poker_pal_app.PocketPokerPalApp.service.PineconeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ask")
@RequiredArgsConstructor
public class AskController {

    private final OpenAIEmbeddingService embeddingService;
    private final PineconeService pineconeService; // you’ll need to create this
    private final OpenAIAnswerService openAIAnswerService;   // you’ll need to create this

    @PostMapping
    public ResponseEntity<String> askQuestion(@RequestBody Map<String, String> request) throws IOException {
        String question = request.get("question");

        List<Double> questionEmbedding = embeddingService.generateEmbedding(question);
        List<String> relevantChunks = pineconeService.queryRelevantChunks(questionEmbedding);

        String answer = openAIAnswerService.getAnswer(question, relevantChunks);

        return ResponseEntity.ok(answer);
    }
}

