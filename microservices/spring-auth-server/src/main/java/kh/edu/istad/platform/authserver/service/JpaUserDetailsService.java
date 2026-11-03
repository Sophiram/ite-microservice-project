package kh.edu.istad.platform.authserver.service;

import kh.edu.istad.platform.authserver.domain.UserAccount;
import kh.edu.istad.platform.authserver.repository.UserAccountRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** Loads login users from PostgreSQL through Spring Data JPA. */
@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    public JpaUserDetailsService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount account = this.userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return User.withUsername(account.getUsername())
                .password(account.getPassword())
                .disabled(!account.isEnabled())
                .authorities(account.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .toList())
                .build();
    }
}
