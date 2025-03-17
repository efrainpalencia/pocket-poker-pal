package com.pocket_poker_pal_app.PocketPokerPalApp.repository;

import com.pocket_poker_pal_app.PocketPokerPalApp.entity.ClientUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClientUserRepository extends JpaRepository<ClientUser, UUID> {

    boolean existsByEmail(String email);
    Optional<ClientUser> findByEmail(String email);

    Optional<ClientUser> findByVerificationToken(String token);

    Optional<ClientUser> findByResetToken(String token);
}
