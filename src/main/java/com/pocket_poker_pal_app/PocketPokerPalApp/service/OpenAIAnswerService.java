package com.pocket_poker_pal_app.PocketPokerPalApp.service;

import com.pocket_poker_pal_app.PocketPokerPalApp.util.PromptBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenAIAnswerService {

    private final ChatClient chatClient;

    /**
     * Builds a prompt using retrieved rulebook chunks and sends it to OpenAI.
     *
     * @param question       the user’s question
     * @param relevantChunks list of rulebook chunks from Pinecone
     * @return AI-generated answer
     */
    public String getAnswer(String question, List<String> relevantChunks) {
        String promptText = PromptBuilder.buildRuleAnswerPrompt(question, relevantChunks);
        Prompt prompt = new Prompt(new UserMessage(promptText));
        return chatClient.call(prompt).getResult().getOutput().getContent();
    }
}
