package com.pocket_poker_pal_app.PocketPokerPalApp.service;

import com.pocket_poker_pal_app.PocketPokerPalApp.entity.User;

import java.util.Optional;

public interface UserService {
    Optional<? extends User> findUserByEmail(String email);
    Optional<? extends User> findUserByUsername(String username);
    Optional<? extends User> findUserByVerificationToken(String token);
    Optional<? extends User> findUserByResetToken(String resetToken);

    boolean emailExists(String email);
    boolean usernameExists(String username);

    void requestResetToken(User user);
    void updatePassword(User user, String newPassword);
}
