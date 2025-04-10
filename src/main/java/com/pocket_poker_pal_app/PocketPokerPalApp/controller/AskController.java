package com.pocket_poker_pal_app.PocketPokerPalApp.controller;

import com.pocket_poker_pal_app.PocketPokerPalApp.service.OpenAIAnswerService;
import com.pocket_poker_pal_app.PocketPokerPalApp.service.OpenAIEmbeddingService;
import com.pocket_poker_pal_app.PocketPokerPalApp.service.PineconeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ask")
@RequiredArgsConstructor
public class AskController {

    private final OpenAIEmbeddingService embeddingService;
    private final PineconeService pineconeService;
    private final OpenAIAnswerService openAIAnswerService;

    @CrossOrigin(origins = "*")
    @PostMapping
    public ResponseEntity<Map<String, String>> askQuestion(@RequestBody Map<String, String> request) throws IOException {
        String question = request.get("question");

        List<Double> questionEmbedding = embeddingService.generateEmbedding(question);
        List<String> relevantChunks = pineconeService.queryRelevantChunks(questionEmbedding);

        String answer = openAIAnswerService.getAnswer(question, relevantChunks);

        return ResponseEntity.ok(Map.of("answer", answer));
    }
}


