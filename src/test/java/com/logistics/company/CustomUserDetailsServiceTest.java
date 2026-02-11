package com.logistics.company;

import com.logistics.company.data.Role;
import com.logistics.company.data.RoleType;
import com.logistics.company.data.User;
import com.logistics.company.repository.UserRepository;
import com.logistics.company.service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        customUserDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {

        Role role = Role.builder()
                .name(RoleType.CUSTOMER)
                .build();

        User user = User.builder()
                .username("ivan")
                .password("encodedPassword")
                .role(role)
                .build();

        when(userRepository.findByUsername("ivan"))
                .thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername("ivan");

        assertEquals("ivan", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals(1, result.getAuthorities().size());
        assertTrue(result.getAuthorities()
                .iterator()
                .next()
                .getAuthority()
                .equals("ROLE_CUSTOMER"));
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserDoesNotExist() {

        when(userRepository.findByUsername("missingUser"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("missingUser")
        );
    }

    @Test
    void loadUserByUsername_ShouldReturnCorrectAuthority_WhenAdminRole() {

        Role role = Role.builder()
                .name(RoleType.ADMIN)
                .build();

        User user = User.builder()
                .username("admin")
                .password("adminPass")
                .role(role)
                .build();

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername("admin");

        assertEquals("ROLE_ADMIN",
                result.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void loadUserByUsername_ShouldReturnCorrectPassword() {

        Role role = Role.builder()
                .name(RoleType.CUSTOMER)
                .build();

        User user = User.builder()
                .username("maria")
                .password("securePass")
                .role(role)
                .build();

        when(userRepository.findByUsername("maria"))
                .thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername("maria");

        assertEquals("securePass", result.getPassword());
    }
}
