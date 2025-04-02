package com.pocket_poker_pal_app.PocketPokerPalApp.service;

import com.pocket_poker_pal_app.PocketPokerPalApp.entity.AdminUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdminUserService {

    AdminUser createAdminUser(AdminUser adminUser);

    AdminUser getAdminUserById(UUID id);

    Optional<AdminUser> findByEmail(String email);

    List<AdminUser> getAllAdminUsers();

    void updateAdminUser(UUID id, AdminUser updatedAdmin);

    void deleteAdminUser(UUID id);
}
