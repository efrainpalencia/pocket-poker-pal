package com.pocket_poker_pal_app.PocketPokerPalApp.controller;

import com.pocket_poker_pal_app.PocketPokerPalApp.service.AudioQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AudioQuestionController {

    private final AudioQuestionService audioQuestionService;

    @PostMapping("/ask-audio")
    public ResponseEntity<?> askViaAudio(@RequestParam("audio") MultipartFile audioFile) {
        if (audioFile.isEmpty() || !audioFile.getContentType().startsWith("audio/")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Please upload a valid audio file."));
        }

        try {
            Map<String, String> response = audioQuestionService.handleAudioQuestion(audioFile);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to process audio: " + e.getMessage()));
        }
    }

}
