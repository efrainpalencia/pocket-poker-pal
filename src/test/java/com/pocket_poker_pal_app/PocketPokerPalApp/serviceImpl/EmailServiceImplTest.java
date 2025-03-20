package com.pocket_poker_pal_app.PocketPokerPalApp.serviceImpl;

import com.pocket_poker_pal_app.PocketPokerPalApp.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@ActiveProfiles("dev")
class EmailServiceImplTest {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailService emailService;

    private String testEmail;

    @BeforeEach
    void setUp() {
        testEmail = "crazydealer2003@gmail.com"; // Or go back to protonmail
        System.out.println("📧 Testing email sending to: " + testEmail);
    }

    @AfterEach
    void tearDown() {
        System.out.println("✅ Test completed for: " + testEmail);
    }

    @Test
    void sendVerificationEmail() {
        String verificationLink = "http://localhost:8080/verify?token=test-token";

        assertDoesNotThrow(() -> {
            emailService.sendVerificationEmail(testEmail, verificationLink);
        });

        System.out.println("✅ Verification email test sent successfully!");
    }

    @Test
    void sendPasswordResetEmail() {
        String resetLink = "http://localhost:8080/reset-password?token=test-reset-token";

        assertDoesNotThrow(() -> {
            emailService.sendPasswordResetEmail(testEmail, resetLink);
        });

        System.out.println("✅ Password reset email test sent successfully!");
    }

    @Test
    void sendDirectHtmlEmailWithJavaMailSender() {
        String subject = "Direct Email Test - Pocket Poker Pal";
        String htmlContent = """
        <html>
        <body>
            <h1>This is a direct test email</h1>
            <p>If you received this, the direct mail sender works!</p>
        </body>
        </html>
        """;

        assertDoesNotThrow(() -> {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(testEmail);
            helper.setSubject(subject);
            helper.setFrom("efrain.palencia@efrainsolves.com"); // ✅ Explicit from address!
            helper.setText(htmlContent, true);

            mailSender.send(message);
        });

        System.out.println("✅ Direct HTML email sent successfully!");
    }

}
