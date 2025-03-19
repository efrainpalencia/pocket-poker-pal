package com.pocket_poker_pal_app.PocketPokerPalApp.controller;

import com.pocket_poker_pal_app.PocketPokerPalApp.dto.AuthResponse;
import com.pocket_poker_pal_app.PocketPokerPalApp.dto.MessageResponse;
import com.pocket_poker_pal_app.PocketPokerPalApp.dto.RegisterRequest;
import com.pocket_poker_pal_app.PocketPokerPalApp.entity.AdminUser;
import com.pocket_poker_pal_app.PocketPokerPalApp.entity.ClientUser;
import com.pocket_poker_pal_app.PocketPokerPalApp.entity.UserEntity;
import com.pocket_poker_pal_app.PocketPokerPalApp.security.CustomUserDetailsService;
import com.pocket_poker_pal_app.PocketPokerPalApp.security.JwtService;
import com.pocket_poker_pal_app.PocketPokerPalApp.service.*;
import com.pocket_poker_pal_app.PocketPokerPalApp.serviceImpl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final AdminUserService adminUserService;
    private final ClientUserService clientUserService;
    private final UserServiceImpl userServiceImpl; // ✅ Using shared logic here
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private static final int VERIFICATION_EXPIRY_HOURS = 24;



    // ✅ Register Admin User
    @PostMapping("/register/admin")
    public Object registerAdmin(@RequestBody @Valid RegisterRequest request) {

        if (userServiceImpl.emailExists(request.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Email already exists"));
        }

        if (userServiceImpl.usernameExists(request.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Username already exists"));
        }

        AdminUser adminUser = new AdminUser();
        adminUser.setEmail(request.getEmail());
        adminUser.setUsername(request.getUsername());
        adminUser.setPassword(passwordEncoder.encode(request.getPassword()));
        adminUser.setRole(UserEntity.Role.ADMIN);

        AdminUser savedAdmin = adminUserService.createAdminUser(adminUser);
        UserDetails userDetails = userDetailsService.loadUserByUsername(savedAdmin.getEmail());

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        System.console().printf("New token: %s\n", accessToken);
        System.console().printf("Refresh token: %s\n", refreshToken);


        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(accessToken, refreshToken));
    }

    // ✅ Register Client User
    @PostMapping("/register/client")
    public ResponseEntity<String> registerClient(@RequestBody @Valid RegisterRequest request) {

        if (userServiceImpl.emailExists(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        if (userServiceImpl.usernameExists(request.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        String verificationToken = UUID.randomUUID().toString();

        ClientUser clientUser = new ClientUser();
        clientUser.setEmail(request.getEmail());
        clientUser.setUsername(request.getUsername());
        clientUser.setPassword(passwordEncoder.encode(request.getPassword()));
        clientUser.setRole(UserEntity.Role.CLIENT);
        clientUser.setEnabled(false);
        clientUser.setVerificationToken(verificationToken);
        clientUser.setVerificationTokenExpiry(LocalDateTime.now().plusHours(VERIFICATION_EXPIRY_HOURS));

        clientUserService.createClientUser(clientUser);

        String verificationLink = "https://yourdomain.com/verify-email?token=" + verificationToken;
        emailService.sendVerificationEmail(clientUser.getEmail(), verificationLink);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Client registered successfully! Please verify your email within 24 hours.");
    }

    // ✅ Verify user by token
    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam("token") String token) {

        Optional<? extends UserEntity> userOpt = userServiceImpl.findUserByVerificationToken(token);

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid verification token.");
        }

        UserEntity user = userOpt.get();

        if (user.isEnabled()) {
            return ResponseEntity.badRequest().body("Account is already verified.");
        }

        if (user.getVerificationTokenExpiry() == null || user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Verification token expired. Please request a new verification link.");
        }

        user.setEnabled(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);

        // Save using appropriate service
        if (user.getRole() == UserEntity.Role.ADMIN) {
            adminUserService.updateAdminUser(user.getId(), (AdminUser) user);
        } else {
            clientUserService.updateClientUser(user.getId(), (ClientUser) user);
        }

        return ResponseEntity.ok("Account verified successfully. You can now log in.");
    }

    // ✅ Resend verification link
    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerification(@RequestParam("email") String email) {

        Optional<? extends UserEntity> userOpt = userServiceImpl.findUserByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with email: " + email);
        }

        UserEntity user = userOpt.get();

        if (user.isEnabled()) {
            return ResponseEntity.badRequest().body("Account already verified.");
        }

        if (user.getVerificationTokenExpiry() != null &&
                user.getVerificationTokenExpiry().isAfter(LocalDateTime.now().minusMinutes(5))) {
            return ResponseEntity.badRequest().body("Please wait before requesting a new verification link.");
        }

        String newToken = UUID.randomUUID().toString();
        user.setVerificationToken(newToken);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(VERIFICATION_EXPIRY_HOURS));

        if (user.getRole() == UserEntity.Role.ADMIN) {
            adminUserService.updateAdminUser(user.getId(), (AdminUser) user);
        } else {
            clientUserService.updateClientUser(user.getId(), (ClientUser) user);
        }

        String verificationLink = "https://yourdomain.com/verify-email?token=" + newToken;
        emailService.sendVerificationEmail(user.getEmail(), verificationLink);

        return ResponseEntity.ok("Verification email resent successfully. Please check your inbox.");
    }

    // ✅ Request password reset
    @PostMapping("/password-reset-request")
    public ResponseEntity<String> requestPasswordReset(@RequestParam("email") String email) {

        Optional<? extends UserEntity> userOpt = userServiceImpl.findUserByEmail(email);

        if (userOpt.isEmpty() || !userOpt.get().isEnabled()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with email: " + email);
        }

        UserEntity user = userOpt.get();

        userServiceImpl.sendGeneratedPasswordResetToken(user);

        return ResponseEntity.ok("Password reset link sent. It will expire in 1 hour.");
    }


    // ✅ Resend password reset link
    @PostMapping("/resend-password-reset")
    public ResponseEntity<String> resendPasswordReset(@RequestParam("email") String email) {

        Optional<? extends UserEntity> userOpt = userServiceImpl.findUserByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with email: " + email);
        }

        UserEntity user = userOpt.get();

        // Optional: add throttling logic here
        if (user.getResetTokenExpiry() != null &&
                user.getResetTokenExpiry().isAfter(LocalDateTime.now().minusMinutes(5))) {
            return ResponseEntity.badRequest().body("Please wait before requesting another reset link.");
        }

        userServiceImpl.sendGeneratedPasswordResetToken(user);

        return ResponseEntity.ok("Password reset link resent successfully.");
    }



    // ✅ Reset password with token
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword) {

        Optional<? extends UserEntity> userOpt = userServiceImpl.findUserByResetToken(token);

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid password reset token.");
        }

        UserEntity user = userOpt.get();

        if (user.getResetTokenExpiry() == null ||
                user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Password reset token expired. Please request a new one.");
        }

        userServiceImpl.updatePassword(user, newPassword);

        return ResponseEntity.ok("Password reset successful.");
    }

}
