package com.pocket_poker_pal_app.PocketPokerPalApp.serviceImpl;

import com.pocket_poker_pal_app.PocketPokerPalApp.entity.AdminUser;
import com.pocket_poker_pal_app.PocketPokerPalApp.repository.AdminUserRepository;
import com.pocket_poker_pal_app.PocketPokerPalApp.service.AdminUserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final AdminUserRepository adminUserRepository;

    @Override
    public AdminUser createAdminUser(AdminUser adminUser) {
        return adminUserRepository.save(adminUser);
    }

    @Override
    public AdminUser getAdminUserById(UUID id) {
        return adminUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Admin user not found with id: " + id));
    }

    @Override
    public List<AdminUser> getAllAdminUsers() {
        return adminUserRepository.findAll();
    }

    @Override
    public void updateAdminUser(UUID id, AdminUser updatedAdmin) {
        AdminUser existingAdmin = getAdminUserById(id);
        existingAdmin.setFirstName(updatedAdmin.getFirstName());
        existingAdmin.setLastName(updatedAdmin.getLastName());
        existingAdmin.setEmail(updatedAdmin.getEmail());
        existingAdmin.setPassword(updatedAdmin.getPassword());
        existingAdmin.setRole(updatedAdmin.getRole());
        adminUserRepository.save(existingAdmin);
    }

    @Override
    public void deleteAdminUser(UUID id) {
        if (!adminUserRepository.existsById(id)) {
            throw new EntityNotFoundException("Admin user not found with id: " + id);
        } else {
            adminUserRepository.deleteById(id);
        }
    }

}
