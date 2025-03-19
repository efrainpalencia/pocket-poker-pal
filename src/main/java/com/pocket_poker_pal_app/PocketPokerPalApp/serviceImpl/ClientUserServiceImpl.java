package com.pocket_poker_pal_app.PocketPokerPalApp.serviceImpl;

import com.pocket_poker_pal_app.PocketPokerPalApp.entity.ClientUser;
import com.pocket_poker_pal_app.PocketPokerPalApp.repository.ClientUserRepository;
import com.pocket_poker_pal_app.PocketPokerPalApp.service.ClientUserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientUserServiceImpl implements ClientUserService {

    private final ClientUserRepository clientUserRepository;

    @Override
    public ClientUser createClientUser(ClientUser clientUser) {
        return clientUserRepository.save(clientUser);
    }

    @Override
    public ClientUser getClientUserById(UUID id) {
        return clientUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client user not found with id: " + id));
    }

    @Override
    public List<ClientUser> getAllClientUsers() {
        return clientUserRepository.findAll();
    }

    @Override
    public void updateClientUser(UUID id, ClientUser updatedClient) {
        ClientUser existingClient = getClientUserById(id);
        existingClient.setUsername(updatedClient.getUsername());
        existingClient.setEmail(updatedClient.getEmail());
        existingClient.setPassword(updatedClient.getPassword());
        existingClient.setRole(updatedClient.getRole());

        clientUserRepository.save(existingClient);
    }

    @Override
    public void deleteClientUser(UUID id) {
        if (!clientUserRepository.existsById(id)) {
            throw new EntityNotFoundException("Client user not found with id: " + id);
        }
        clientUserRepository.deleteById(id);
    }

    @Override
    public Optional<ClientUser> getClientUserByEmail(String email) {
        if (!clientUserRepository.existsByEmail(email)) {
            throw new EntityNotFoundException("Client user not found with email: " + email);
        }
        return clientUserRepository.findByEmail(email);
    }

    @Override
    public Optional<ClientUser> findByVerificationToken(String verificationToken) {
        if (!clientUserRepository.existsByVerificationToken(verificationToken)) {
            throw new EntityNotFoundException("Client user not found with verificationToken: " + verificationToken);
        }
        return clientUserRepository.findByVerificationToken(verificationToken);
    }

    @Override
    public Optional<ClientUser> findByResetToken(String resetToken) {
        if (!clientUserRepository.existsByResetToken(resetToken)) {
            throw new EntityNotFoundException("Client user not found with resetToken: " + resetToken);
        }
            return clientUserRepository.findByResetToken(resetToken);
    }
}
