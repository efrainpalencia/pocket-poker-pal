package com.pocket_poker_pal_app.PocketPokerPalApp.service;

import com.pocket_poker_pal_app.PocketPokerPalApp.entity.ClientUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientUserService {

    ClientUser createClientUser(ClientUser clientUser);

    ClientUser getClientUserById(UUID id);

    List<ClientUser> getAllClientUsers();

    void updateClientUser(UUID id, ClientUser updatedClient);

    void deleteClientUser(UUID id);

    Optional<ClientUser> getClientUserByEmail(String email);

    Optional<ClientUser> findByVerificationToken(String verificationToken);

    Optional<ClientUser> findByResetToken(String resetToken);
}
