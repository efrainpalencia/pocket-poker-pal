package com.pocket_poker_pal_app.PocketPokerPalApp.util;

import java.util.List;

public class PromptBuilder {

    public static String buildRuleAnswerPrompt(String question, List<String> relevantChunks) {
        StringBuilder context = new StringBuilder();
        for (String chunk : relevantChunks) {
            context.append("- ").append(chunk.trim()).append("\n\n");
        }

        return """
You are a highly knowledgeable poker rules assistant. Your task is to answer the user's question clearly, accurately, and decisively using the provided rulebook content.

Use the **Cognitive Verifier Pattern** to ensure your response is logically sound:
1. Break down the user's question into smaller parts or sub-questions.
2. Identify key terms or ambiguous elements that may affect interpretation.
3. Determine if the game is a cash game or tournament game since different rules may apply depending on this factor.
4. Review the rulebook content carefully to locate relevant rules or examples.
5. Verify that the rulebook supports your reasoning for each part of your answer.
6. Only then, apply the rules and make a final ruling. You may provide two answers if a tournament rule and a cash game rule are different — explain your logic clearly and confidently.

You must make a decision yourself. Do not defer or remain uncertain.

Rulebook Content: \s
%s

Question: \s
%s

Answer (with verified reasoning and final decision):
           \s""".formatted(context.toString(), question);
    }
}
