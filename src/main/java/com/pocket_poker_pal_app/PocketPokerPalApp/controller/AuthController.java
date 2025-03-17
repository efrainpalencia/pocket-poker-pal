package com.pocket_poker_pal_app.PocketPokerPalApp.controller;

import com.pocket_poker_pal_app.PocketPokerPalApp.dto.AuthResponse;
import com.pocket_poker_pal_app.PocketPokerPalApp.dto.RegisterRequest;
import com.pocket_poker_pal_app.PocketPokerPalApp.entity.AdminUser;
import com.pocket_poker_pal_app.PocketPokerPalApp.entity.ClientUser;
import com.pocket_poker_pal_app.PocketPokerPalApp.entity.UserEntity;
import com.pocket_poker_pal_app.PocketPokerPalApp.repository.ClientUserRepository;
import com.pocket_poker_pal_app.PocketPokerPalApp.security.CustomUserDetailsService;
import com.pocket_poker_pal_app.PocketPokerPalApp.security.JwtService;
import com.pocket_poker_pal_app.PocketPokerPalApp.service.AdminUserService;
import com.pocket_poker_pal_app.PocketPokerPalApp.service.ClientUserService;
import com.pocket_poker_pal_app.PocketPokerPalApp.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final AdminUserService adminUserService;
    private final ClientUserService clientUserService;
    private final PasswordEncoder passwordEncoder;
    private final ClientUserRepository clientUserRepository;
    private final EmailService emailService;

    // ✅ Register Admin User
    @PostMapping("/register/admin")
    public ResponseEntity<AuthResponse> registerAdmin(@RequestBody @Valid RegisterRequest request) {
        AdminUser adminUser = new AdminUser();
        adminUser.setEmail(request.getEmail());
        adminUser.setUsername(request.getUsername());
        adminUser.setPassword(passwordEncoder.encode(request.getPassword()));
        adminUser.setRole(UserEntity.Role.ADMIN);

        AdminUser savedAdmin = adminUserService.createAdminUser(adminUser);
        UserDetails userDetails = userDetailsService.loadUserByUsername(savedAdmin.getEmail());

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    // ✅ Register Client User
    @PostMapping("/register/client")
    public ResponseEntity<String> registerClient(@RequestBody @Valid RegisterRequest request) {
        String verificationToken = UUID.randomUUID().toString();

        ClientUser clientUser = new ClientUser();
        clientUser.setEmail(request.getEmail());
        clientUser.setUsername(request.getUsername());
        clientUser.setPassword(passwordEncoder.encode(request.getPassword()));
        clientUser.setRole(UserEntity.Role.CLIENT);
        clientUser.setEnabled(false);
        clientUser.setVerificationToken(verificationToken);

        ClientUser savedClient = clientUserService.createClientUser(clientUser);

        // Send email verification link
        String verificationLink = "https://yourdomain.com/verify-email?token=" + verificationToken;
        emailService.sendVerificationEmail(savedClient.getEmail(), verificationLink);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Client registered successfully! Please verify your email.");
    }

    // ✅ Email verification
    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam("token") String token) {
        Optional<ClientUser> userOpt = clientUserRepository.findByVerificationToken(token);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid or expired verification token.");
        }

        ClientUser user = userOpt.get();
        user.setEnabled(true);
        user.setVerificationToken(null);
        clientUserRepository.save(user);

        return ResponseEntity.ok("Account verified successfully. You can now log in.");
    }

    // ✅ Request password reset
    @PostMapping("/password-reset-request")
    public ResponseEntity<String> requestPasswordReset(@RequestParam("email") String email) {
        Optional<ClientUser> userOpt = clientUserRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with email: " + email);
        }

        ClientUser user = userOpt.get();
        String resetToken = UUID.randomUUID().toString();

        user.setResetToken(resetToken);
        clientUserRepository.save(user);

        String resetLink = "https://yourdomain.com/reset-password?token=" + resetToken;
        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);

        return ResponseEntity.ok("Password reset link sent to your email.");
    }

    // ✅ Reset password
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword) {

        Optional<ClientUser> userOpt = clientUserRepository.findByResetToken(token);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid or expired password reset token.");
        }

        ClientUser user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        clientUserRepository.save(user);

        return ResponseEntity.ok("Password reset successfully.");
    }

}
