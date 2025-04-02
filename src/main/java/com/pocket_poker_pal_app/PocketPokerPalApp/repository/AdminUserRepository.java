package com.pocket_poker_pal_app.PocketPokerPalApp.repository;

import com.pocket_poker_pal_app.PocketPokerPalApp.entity.AdminUser;
import com.pocket_poker_pal_app.PocketPokerPalApp.entity.ClientUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AdminUserRepository extends JpaRepository<AdminUser, UUID> {

    boolean existsByEmail(String email);

    boolean existsByFirstName(String firstName);

    boolean existsByLastName(String lastName);

    boolean existsByVerificationToken(String verificationToken);

    Optional<AdminUser> findByEmail(String email);

    Optional<AdminUser> findByFirstName(String firstName);

    Optional<AdminUser> findByLastName(String lastName);

    Optional<AdminUser> findByVerificationToken(String token);

    Optional<AdminUser> findByResetToken(String token);
}
