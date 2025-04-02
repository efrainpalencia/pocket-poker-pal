package com.pocket_poker_pal_app.PocketPokerPalApp.controller;

import com.pocket_poker_pal_app.PocketPokerPalApp.entity.ClientUser;
import com.pocket_poker_pal_app.PocketPokerPalApp.service.ClientUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/client-users")
@RequiredArgsConstructor
public class ClientUserController {

    private final ClientUserService clientUserService;

    // ✅ Get all Client Users
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ClientUser>> getAllClientUsers() {
        List<ClientUser> users = clientUserService.getAllClientUsers();
        return ResponseEntity.ok(users);
    }
    // ✅ Get Client User by ID
    @GetMapping("/view/{id}")
    public ResponseEntity<ClientUser> getClientUserById(@PathVariable UUID id) {
        ClientUser clientUser = clientUserService.getClientUserById(id);
        return ResponseEntity.ok(clientUser);
    }

    // ✅ Create Client User
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<String> createClientUser(@Valid @RequestBody ClientUser clientUser) {
        ClientUser createdUser = clientUserService.createClientUser(clientUser);
        return ResponseEntity.status(201).body("Client user created successfully! ID: " + createdUser.getId());
    }
    // ✅ Update Client User
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateClientUser(@PathVariable UUID id, @Valid @RequestBody ClientUser clientUser) {
        clientUserService.updateClientUser(id, clientUser);
        return ResponseEntity.status(201).body("Client user updated successfully! ID: " + id);
    }
    // ✅ Delete Client User
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteClientUser(@PathVariable UUID id) {
        clientUserService.deleteClientUser(id);
        return ResponseEntity.status(201).body("Client user deleted successfully! ID: " + id);
    }
}
