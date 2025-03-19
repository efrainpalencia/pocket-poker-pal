package com.pocket_poker_pal_app.PocketPokerPalApp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "client_users")
@Data
@EqualsAndHashCode(callSuper = true)
public class ClientUser extends UserEntity {

}
