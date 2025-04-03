package com.pocket_poker_pal_app.PocketPokerPalApp.util;

import java.util.List;

public class PromptBuilder {

    public static String buildRuleAnswerPrompt(String question, List<String> relevantChunks) {
        StringBuilder context = new StringBuilder();
        for (String chunk : relevantChunks) {
            context.append("- ").append(chunk.trim()).append("\n\n");
        }

        return """
            You are a highly knowledgeable poker rules assistant. Use the provided rulebook content to answer the user's question clearly and accurately. You can use your knowledge to apply a ruling based on the context. It is critical that you make a decision yourself.
            
            Rulebook Content:
            %s

            Question: %s

            Answer:
            """.formatted(context.toString(), question);
    }
}
