package com.pocket_poker_pal_app.PocketPokerPalApp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "admin_users")
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminUser extends User {

    @Column( nullable = false)
    private String firstName;

    @Column( nullable = false)
    private String lastName;
}
