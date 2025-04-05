package com.pocket_poker_pal_app.PocketPokerPalApp.controller;

import com.pocket_poker_pal_app.PocketPokerPalApp.service.AudioQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AudioQuestionController {

    private final AudioQuestionService audioQuestionService;

    @PostMapping("/ask-audio")
    public ResponseEntity<String> askViaAudio(@RequestParam("audio") MultipartFile audioFile) {
        try {
            String answer = audioQuestionService.handleAudioQuestion(audioFile);
            return ResponseEntity.ok(answer);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to process audio: " + e.getMessage());
        }
    }
}
