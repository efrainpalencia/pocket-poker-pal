package com.pocket_poker_pal_app.PocketPokerPalApp.serviceImpl;

import com.pocket_poker_pal_app.PocketPokerPalApp.service.EmailService;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    /**
     * Sends a verification email with an HTML template.
     * @param toEmail the recipient's email address
     * @param verificationLink the verification link
     */
    @Override
    public void sendVerificationEmail(String toEmail, String verificationLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("Verify your account - Pocket Poker Pal");

            String htmlContent = """
            <html>
            <body>
                <h2>Welcome to Pocket Poker Pal!</h2>
                <p>Click the link below to verify your account:</p>
                <a href="%s">Verify Account</a>
                <p>This link will expire in 24 hours.</p>
            </body>
            </html>
            """.formatted(verificationLink);

            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Verification email sent to {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send verification email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Sends a password reset email with a reset link.
     * @param to the recipient's email address
     * @param resetLink the password reset link
     */
    @Override
    public void sendPasswordResetEmail(String to, String resetLink) {
        String subject = "Password Reset Request";
        String message = "You requested to reset your password.\n\nClick the link below to reset it:\n" + resetLink;

        sendEmail(to, subject, message);
    }

    /**
     * Generic method for sending plain text emails.
     * @param to recipient's email
     * @param subject email subject
     * @param text email body
     */
    private void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(text);

            mailSender.send(mailMessage);
            log.info("Email sent to {}", to);

        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
