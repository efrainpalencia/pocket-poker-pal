package com.pocket_poker_pal_app.PocketPokerPalApp.service;

import com.pocket_poker_pal_app.PocketPokerPalApp.entity.UserEntity;

import java.util.Optional;

public interface UserService {
    Optional<? extends UserEntity> findUserByEmail(String email);
    Optional<? extends UserEntity> findUserByUsername(String username);
    Optional<? extends UserEntity> findUserByVerificationToken(String token);
    Optional<? extends UserEntity> findUserByResetToken(String resetToken);

    boolean emailExists(String email);
    boolean usernameExists(String username);

    void requestResetToken(UserEntity user);
    void updatePassword(UserEntity user, String newPassword);
}
