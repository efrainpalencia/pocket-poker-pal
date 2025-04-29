//package com.pocket_poker_pal_app.PocketPokerPalApp.console;
//
//import com.pocket_poker_pal_app.PocketPokerPalApp.service.OpenAIAnswerService;
//import com.pocket_poker_pal_app.PocketPokerPalApp.service.RulebookSearchService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayDeque;
//import java.util.Deque;
//import java.util.List;
//import java.util.Scanner;
//
//@Component
//@RequiredArgsConstructor
//public class RulebookChatConsoleApp implements CommandLineRunner {
//
//    private final RulebookSearchService rulebookSearchService;
//    private final OpenAIAnswerService openAIAnswerService;
//    private final Deque<String> chatMemory = new ArrayDeque<>();
//
//    private static final int MAX_MESSAGES = 20; // 10 exchanges = 20 lines (user + assistant)
//
//    @Override
//    public void run(String... args) {
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("🤖 Welcome to the Pocket Poker Pal Console Chat!");
//        System.out.println("Type your question below (or type 'exit' to quit):");
//
//        while (true) {
//            System.out.print("\nYou: ");
//            String question = scanner.nextLine();
//
//            if (question.equalsIgnoreCase("exit")) {
//                System.out.println("👋 Thanks for chatting. Goodbye!");
//                break;
//            }
//
//            try {
//                // Save user question
//                addToMemory("User: " + question);
//
//                // Find relevant rulebook chunks
//                List<String> relevantChunks = rulebookSearchService.searchRelevantChunks(question, 5);
//
//                // Build full prompt from memory
//                StringBuilder fullPrompt = new StringBuilder();
//                chatMemory.forEach(line -> fullPrompt.append(line).append("\n"));
//                fullPrompt.append("Assistant:");
//
//                // Generate answer from OpenAI
//                String answer = openAIAnswerService.getAnswer(fullPrompt.toString(), relevantChunks);
//
//                // Output and store response
//                System.out.println("\n🎯 PokerPal: " + answer);
//                addToMemory("Assistant: " + answer);
//
//            } catch (Exception e) {
//                System.out.println("⚠️ Error: " + e.getMessage());
//            }
//        }
//    }
//
//    private void addToMemory(String line) {
//        if (chatMemory.size() >= MAX_MESSAGES) {
//            chatMemory.pollFirst(); // remove oldest line
//        }
//        chatMemory.addLast(line);
//    }
//}
