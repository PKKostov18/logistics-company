// CustomUserDetailsService.java
package com.logistics.company.service;

import com.logistics.company.data.User;
import com.logistics.company.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads the user by their username (as required by Spring Security).
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Find the user in our database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // 2. Convert our Role entity into a Spring Security GrantedAuthority
        // We add the "ROLE_" prefix, which is a standard Spring Security convention.
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().getName().name());

        // 3. Return a Spring Security User object
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                Collections.singletonList(authority) // The list of roles
        );
    }
}