package com.pocket_poker_pal_app.PocketPokerPalApp.repository;

import com.pocket_poker_pal_app.PocketPokerPalApp.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AdminUserRepository extends JpaRepository<AdminUser, UUID> {

    boolean existsByEmail(String email);
    Optional<AdminUser> findByEmail(String email);
}
