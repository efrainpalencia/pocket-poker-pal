package com.pocket_poker_pal_app.PocketPokerPalApp.security;

import com.pocket_poker_pal_app.PocketPokerPalApp.entity.AdminUser;
import com.pocket_poker_pal_app.PocketPokerPalApp.entity.ClientUser;
import com.pocket_poker_pal_app.PocketPokerPalApp.repository.AdminUserRepository;
import com.pocket_poker_pal_app.PocketPokerPalApp.repository.ClientUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminUserRepository adminUserRepository;
    private final ClientUserRepository clientUserRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AdminUser admin = adminUserRepository.findByEmail(email).orElse(null);
        if (admin != null) {
            return new UserPrincipal(admin);
        }

        ClientUser client = clientUserRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));
        return new UserPrincipal(client);
    }
}
