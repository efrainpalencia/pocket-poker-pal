package com.pocket_poker_pal_app.PocketPokerPalApp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pocket_poker_pal_app.PocketPokerPalApp.dto.AdminRegisterRequest;
import com.pocket_poker_pal_app.PocketPokerPalApp.dto.ClientRegisterRequest;
import com.pocket_poker_pal_app.PocketPokerPalApp.entity.AdminUser;
import com.pocket_poker_pal_app.PocketPokerPalApp.entity.ClientUser;
import com.pocket_poker_pal_app.PocketPokerPalApp.security.JwtService;
import com.pocket_poker_pal_app.PocketPokerPalApp.security.SecurityConfig;
import com.pocket_poker_pal_app.PocketPokerPalApp.service.AdminUserService;
import com.pocket_poker_pal_app.PocketPokerPalApp.service.ClientUserService;
import com.pocket_poker_pal_app.PocketPokerPalApp.service.EmailService;
import com.pocket_poker_pal_app.PocketPokerPalApp.serviceImpl.UserServiceImpl;
import com.pocket_poker_pal_app.PocketPokerPalApp.security.CustomUserDetailsService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class) // Only if you have specific security config beans you need
@AutoConfigureMockMvc(addFilters = false) // ✅ Disables Spring Security filters
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Mock dependencies
    @MockBean
    private JwtService jwtService;


    @MockBean
    private AdminUserService adminUserService;

    @MockBean
    private ClientUserService clientUserService;

    @MockBean
    private UserServiceImpl userServiceImpl;

    @MockBean
    private EmailService emailService;

    @MockBean
    private ClientRegisterRequest clientRegisterRequest;

    @MockBean
    private AdminRegisterRequest adminRegisterRequest;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        clientRegisterRequest = new ClientRegisterRequest();
        clientRegisterRequest.setEmail("test@example.com");
        clientRegisterRequest.setUsername("testuser");
        clientRegisterRequest.setPassword("password123");

        adminRegisterRequest = new AdminRegisterRequest();
        adminRegisterRequest.setEmail("test@example.com");
        adminRegisterRequest.setFirstName("testfirstname");
        adminRegisterRequest.setLastName("testlastname");
        clientRegisterRequest.setPassword("password123");
    }

    @Test
    void registerAdmin_success() throws Exception {
        AdminUser adminUser = new AdminUser();
        adminUser.setId(UUID.randomUUID());
        adminUser.setEmail(adminRegisterRequest.getEmail());
        adminUser.setFirstName(adminRegisterRequest.getFirstName());
        adminUser.setLastName(adminRegisterRequest.getLastName());

        when(adminUserService.createAdminUser(any(AdminUser.class))).thenReturn(adminUser);
        when(jwtService.generateAccessToken(any())).thenReturn("mock-access-token");
        when(jwtService.generateRefreshToken(any())).thenReturn("mock-refresh-token");

        mockMvc.perform(post("/api/auth/register/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientRegisterRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("mock-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("mock-refresh-token"));

        verify(adminUserService, times(1)).createAdminUser(any(AdminUser.class));
    }

    @Test
    void registerClient_success() throws Exception {
        ClientUser clientUser = new ClientUser();
        clientUser.setId(UUID.randomUUID());
        clientUser.setEmail(clientRegisterRequest.getEmail());
        clientUser.setUsername(clientRegisterRequest.getUsername());

        when(userServiceImpl.emailExists(clientRegisterRequest.getEmail())).thenReturn(false);
        when(userServiceImpl.usernameExists(clientRegisterRequest.getUsername())).thenReturn(false);
        when(clientUserService.createClientUser(any(ClientUser.class))).thenReturn(clientUser);

        mockMvc.perform(post("/api/auth/register/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientRegisterRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Client registered successfully! Please verify your email within 24 hours."));

        verify(emailService, times(1)).sendVerificationEmail(eq(clientRegisterRequest.getEmail()), anyString());
    }

    @Test
    void verifyUser_success() throws Exception {
        ClientUser clientUser = new ClientUser();
        clientUser.setVerificationToken("valid-token");
        clientUser.setEmail("test@example.com");
        clientUser.setUsername("testuser");
        clientUser.setEnabled(false);
        clientUser.setVerificationTokenExpiry(LocalDateTime.now().plusHours(1));

        when(userServiceImpl.findUserByVerificationToken("valid-token"))
                .thenAnswer(invocation -> Optional.of(clientUser));

        mockMvc.perform(get("/api/auth/verify").param("token", "valid-token"))
                .andExpect(status().isOk())
                .andExpect(content().string("Account verified successfully. You can now log in."));

        verify(userServiceImpl, times(1)).findUserByVerificationToken("valid-token");
    }

    @Test
    void resendVerification_success() throws Exception {
        ClientUser clientUser = new ClientUser();
        clientUser.setEmail(clientRegisterRequest.getEmail());
        clientUser.setEnabled(false);

        when(userServiceImpl.findUserByEmail(clientRegisterRequest.getEmail())).thenAnswer(invocation -> Optional.of(clientUser));

        mockMvc.perform(post("/api/auth/resend-verification")
                        .param("email", clientRegisterRequest.getEmail()))
                .andExpect(status().isOk())
                .andExpect(content().string("Verification email resent successfully. Please check your inbox."));

        verify(emailService, times(1)).sendVerificationEmail(eq(clientRegisterRequest.getEmail()), anyString());
    }

    @Test
    void requestPasswordReset_success() throws Exception {
        // Arrange
        ClientUser mockUser = new ClientUser();
        mockUser.setId(UUID.randomUUID());
        mockUser.setEmail("test@example.com");
        mockUser.setUsername("testuser");
        mockUser.setEnabled(true);

        when(userServiceImpl.findUserByEmail("test@example.com"))
                .thenAnswer(invocation -> Optional.of(mockUser));

        // Act
        mockMvc.perform(post("/api/auth/password-reset-request")
                        .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset link sent. It will expire in 1 hour."));

        // Assert
        verify(userServiceImpl, times(1)).sendGeneratedPasswordResetToken(mockUser); // ✅ THIS!
    }







    @Test
    void resendPasswordReset_success() throws Exception {
        ClientUser mockUser = new ClientUser();
        mockUser.setId(UUID.randomUUID());
        mockUser.setEmail("test@example.com");
        mockUser.setUsername("testuser");

        mockUser.setResetTokenExpiry(LocalDateTime.now().minusMinutes(10)); // Older than 5 minutes to pass the throttle check

        when(userServiceImpl.findUserByEmail("test@example.com"))
                .thenAnswer(invocation -> Optional.of(mockUser));

        mockMvc.perform(post("/api/auth/resend-password-reset")
                        .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset link resent successfully."));

        verify(userServiceImpl, times(1)).sendGeneratedPasswordResetToken(mockUser); // ✅ THIS!
    }


    @Test
    void resetPassword_success() throws Exception {
        // Arrange
        String validToken = "valid-token";
        String newPassword = "newPassword123";

        ClientUser mockUser = new ClientUser();
        mockUser.setId(UUID.randomUUID());
        mockUser.setEmail("test@example.com");
        mockUser.setUsername("testuser");
        mockUser.setResetToken(validToken);
        mockUser.setResetTokenExpiry(LocalDateTime.now().plusHours(1));

        when(userServiceImpl.findUserByResetToken(validToken))
                .thenAnswer(invocation -> Optional.of(mockUser));

        // Act & Assert
        mockMvc.perform(post("/api/auth/reset-password")
                        .param("token", validToken)
                        .param("newPassword", newPassword))
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset successful."));

        verify(userServiceImpl, times(1)).updatePassword(mockUser, newPassword);
    }


}
