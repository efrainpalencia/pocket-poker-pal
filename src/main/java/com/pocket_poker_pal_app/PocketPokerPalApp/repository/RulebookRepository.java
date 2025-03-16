package com.pocket_poker_pal_app.PocketPokerPalApp.repository;

import com.pocket_poker_pal_app.PocketPokerPalApp.entity.RulebookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RulebookRepository extends JpaRepository<RulebookEntity, UUID> {
}
