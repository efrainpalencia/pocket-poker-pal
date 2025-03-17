package com.pocket_poker_pal_app.PocketPokerPalApp.service;

import com.pocket_poker_pal_app.PocketPokerPalApp.entity.AdminUser;
import com.pocket_poker_pal_app.PocketPokerPalApp.entity.ClientUser;
import com.pocket_poker_pal_app.PocketPokerPalApp.repository.AdminUserRepository;
import com.pocket_poker_pal_app.PocketPokerPalApp.repository.ClientUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AdminUserRepository adminUserRepo;
    private final ClientUserRepository clientUserRepo;

    public Optional<AdminUser> findAdminByEmail(String email) {
        return adminUserRepo.findByEmail(email);
    }

    public Optional<ClientUser> findClientByEmail(String email) {
        return clientUserRepo.findByEmail(email);
    }

    // Add more logic as needed...
}

