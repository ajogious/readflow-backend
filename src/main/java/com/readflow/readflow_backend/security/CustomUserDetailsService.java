package com.readflow.readflow_backend.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.readflow.readflow_backend.entity.UserStatus;
import com.readflow.readflow_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getStatus() == UserStatus.DEACTIVATED) {
            throw new UsernameNotFoundException("User deactivated");
        }

        boolean enabled = user.getStatus() == UserStatus.ACTIVE;

        return new AuthUser(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole().name(),
                enabled);
    }

}
