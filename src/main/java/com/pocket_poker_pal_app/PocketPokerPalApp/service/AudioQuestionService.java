package com.pocket_poker_pal_app.PocketPokerPalApp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AudioQuestionService {

    private final WhisperService whisperService;
    private final RulebookSearchService rulebookSearchService;
    private final OpenAIAnswerService openAIAnswerService;

    public Map<String, String> handleAudioQuestion(MultipartFile audioFile) throws IOException {
        String question = whisperService.transcribe(audioFile);
        List<String> chunks = rulebookSearchService.searchRelevantChunks(question, 5);
        String answer = openAIAnswerService.getAnswer(question, chunks);

        return Map.of(
                "question", question,
                "answer", answer
        );
    }

}
