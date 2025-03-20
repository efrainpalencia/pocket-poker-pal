package com.pocket_poker_pal_app.PocketPokerPalApp.serviceImpl;

import com.pocket_poker_pal_app.PocketPokerPalApp.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    // ✅ This should match your SMTP username (or verified sender address)
    @Value("${EMAIL_USERNAME}")
    private String fromEmail;

    /**
     * Sends a verification email with an HTML template.
     *
     * @param toEmail          the recipient's email address
     * @param verificationLink the verification link
     */
    @Override
    public void sendVerificationEmail(String toEmail, String verificationLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail); // ✅ Always set the "From"
            helper.setTo(toEmail);
            helper.setSubject("Verify your account - Pocket Poker Pal");

            String htmlContent = String.format("""
                    <html>
                    <body>
                        <h2>Welcome to Pocket Poker Pal!</h2>
                        <p>Click the link below to verify your account:</p>
                        <a href="%s">Verify Account</a>
                        <p>This link will expire in 24 hours.</p>
                    </body>
                    </html>
                    """, verificationLink);

            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("✅ Verification email sent to {}", toEmail);

        } catch (MessagingException e) {
            log.error("❌ Failed to send verification email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    /**
     * Sends a password reset email with a reset link.
     *
     * @param to        the recipient's email address
     * @param resetLink the password reset link
     */
    @Override
    public void sendPasswordResetEmail(String to, String resetLink) {
        String subject = "Password Reset Request - Pocket Poker Pal";
        String text = String.format("""
                You requested to reset your password.

                Click the link below to reset it:
                %s

                If you did not request this, please ignore this email.
                """, resetLink);

        sendPlainTextEmail(to, subject, text);
    }

    /**
     * Generic method for sending plain text emails.
     *
     * @param to      recipient's email
     * @param subject email subject
     * @param text    email body
     */
    private void sendPlainTextEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(fromEmail); // ✅ Ensure the "From" is explicitly set
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(text);

            mailSender.send(mailMessage);
            log.info("✅ Plain text email sent to {}", to);

        } catch (Exception e) {
            log.error("❌ Failed to send plain text email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send plain text email", e);
        }
    }
}
