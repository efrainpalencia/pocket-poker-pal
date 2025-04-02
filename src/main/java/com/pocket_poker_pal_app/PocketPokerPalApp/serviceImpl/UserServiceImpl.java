package com.pocket_poker_pal_app.PocketPokerPalApp.serviceImpl;

import com.pocket_poker_pal_app.PocketPokerPalApp.entity.*;
import com.pocket_poker_pal_app.PocketPokerPalApp.repository.*;
import com.pocket_poker_pal_app.PocketPokerPalApp.security.JwtService;
import com.pocket_poker_pal_app.PocketPokerPalApp.service.*;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AdminUserRepository adminUserRepository;
    private final ClientUserRepository clientUserRepository;
    private final AdminUserService adminUserService;
    private final ClientUserService clientUserService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtService jwtService;

    private static final int RESET_TOKEN_EXPIRY_HOURS = 1;




    @Override
    public Optional<? extends User> findUserByEmail(String email) {
        Optional<AdminUser> admin = adminUserRepository.findByEmail(email);
        if (admin.isPresent()) return admin;

        Optional<ClientUser> client = clientUserRepository.findByEmail(email);
        if (client.isPresent()) return client;

        throw new EntityNotFoundException("No user found with email: " + email);
    }

    @Override
    public Optional<? extends User> findUserByUsername(String username) {

        Optional<ClientUser> client = clientUserRepository.findByUsername(username);
        if (client.isPresent()) return client;

        throw new EntityNotFoundException("No user found with username: " + username);
    }

    @Override
    public Optional<? extends User> findUserByVerificationToken(String token) {
        Optional<AdminUser> admin = adminUserRepository.findByVerificationToken(token);
        if (admin.isPresent()) return admin;

        Optional<ClientUser> client = clientUserRepository.findByVerificationToken(token);
        if (client.isPresent()) return client;

        throw new EntityNotFoundException("No user found with verification token: " + token);
    }

    @Override
    public Optional<? extends User> findUserByResetToken(String resetToken) {
        Optional<AdminUser> admin = adminUserRepository.findByResetToken(resetToken);
        if (admin.isPresent()) return admin;

        Optional<ClientUser> client = clientUserRepository.findByResetToken(resetToken);
        if (client.isPresent()) return client;

        throw new EntityNotFoundException("No user found with reset token: " + resetToken);
    }

    @Override
    public boolean emailExists(String email) {
        return adminUserRepository.existsByEmail(email) || clientUserRepository.existsByEmail(email);
    }

    @Override
    public boolean usernameExists(String username) {
        return clientUserRepository.existsByUsername(username);
    }

    @Override
    public void requestResetToken(User user) {
        String newResetToken = UUID.randomUUID().toString();
        user.setResetToken(newResetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(RESET_TOKEN_EXPIRY_HOURS));

        if (user.getRole() == User.Role.ADMIN) {
            adminUserService.updateAdminUser(user.getId(), (AdminUser) user);
        } else {
            clientUserService.updateClientUser(user.getId(), (ClientUser) user);
        }

        String resetLink = "https://yourdomain.com/reset-password?token=" + newResetToken;
        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
    }

    @Override
    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);

        if (user.getRole() == User.Role.ADMIN) {
            adminUserService.updateAdminUser(user.getId(), (AdminUser) user);
        } else {
            clientUserService.updateClientUser(user.getId(), (ClientUser) user);
        }
    }

    public void sendGeneratedPasswordResetToken(User user) {

        String resetToken = jwtService.generatePasswordResetToken(user);
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));

        // Save user
        if (user.getRole() == User.Role.ADMIN) {
            adminUserService.updateAdminUser(user.getId(), (AdminUser) user);
        } else {
            clientUserService.updateClientUser(user.getId(), (ClientUser) user);
        }

        // Create the reset link
        String resetLink = "https://yourdomain.com/reset-password?token=" + resetToken;

        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);

    }
}
