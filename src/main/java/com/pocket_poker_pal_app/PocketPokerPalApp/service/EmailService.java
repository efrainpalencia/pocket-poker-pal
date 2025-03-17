package com.pocket_poker_pal_app.PocketPokerPalApp.service;

public interface EmailService {
    void sendVerificationEmail(String to, String verificationLink);
    void sendPasswordResetEmail(String to, String resetLink);
}
