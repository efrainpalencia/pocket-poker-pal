package com.pocket_poker_pal_app.PocketPokerPalApp.controller;

import com.pocket_poker_pal_app.PocketPokerPalApp.entity.AdminUser;
import com.pocket_poker_pal_app.PocketPokerPalApp.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin-users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    // ✅ Get all Admin Users
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("")
    public ResponseEntity<List<AdminUser>> getAllAdminUsers() {
        List<AdminUser> users = adminUserService.getAllAdminUsers();
        return ResponseEntity.ok(users);
    }

    // ✅ Get Admin User by ID
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/view/{id}")
    public ResponseEntity<AdminUser> getAdminUserById(@PathVariable UUID id) {
        AdminUser user = adminUserService.getAdminUserById(id);
        return ResponseEntity.ok(user);
    }

    // ✅ Create Admin User
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<String> createAdminUser(@Valid @RequestBody AdminUser adminUser) {
        AdminUser createdUser = adminUserService.createAdminUser(adminUser);
        return ResponseEntity.status(201).body("Admin user created successfully! ID: " + createdUser.getId());
    }

    // ✅ Update Admin User
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateAdminUser(@PathVariable UUID id, @Valid @RequestBody AdminUser adminUser) {
        adminUserService.updateAdminUser(id, adminUser);
        return ResponseEntity.ok("Admin user updated successfully! ID: " + id);
    }

    // ✅ Delete Admin User
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAdminUser(@PathVariable UUID id) {
        adminUserService.deleteAdminUser(id);
        return ResponseEntity.ok("Admin user deleted successfully! ID: " + id);
    }

}
