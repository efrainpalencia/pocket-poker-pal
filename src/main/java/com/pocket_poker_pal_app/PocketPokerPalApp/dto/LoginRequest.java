package com.pocket_poker_pal_app.PocketPokerPalApp.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
