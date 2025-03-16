package com.pocket_poker_pal_app.PocketPokerPalApp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "admin_users")
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminUser extends UserEntity {
    // You can add Admin-specific fields later
}
