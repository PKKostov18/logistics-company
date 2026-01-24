package com.logistics.company.service;

import com.logistics.company.data.*;
import com.logistics.company.repository.RoleRepository;
import com.logistics.company.repository.UserRepository;
import com.logistics.company.dto.RegistrationRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerNewUser(RegistrationRequest registrationRequest) {

        if (userRepository.findByUsername(registrationRequest.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username is already taken.");
        }
        if (userRepository.findByEmail(registrationRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email address is already registered.");
        }

        Role defaultRole = roleRepository.findByName(RoleType.CUSTOMER)
                .orElseThrow(() -> new IllegalStateException("Role 'CUSTOMER' not found in the database."));

        String encodedPassword = passwordEncoder.encode(registrationRequest.getPassword());

        User newUser = User.builder()
                .username(registrationRequest.getUsername())
                .password(encodedPassword)
                .email(registrationRequest.getEmail())
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .phoneNumber(registrationRequest.getPhoneNumber())
                .role(defaultRole)
                .build();

        if (defaultRole.getName() == RoleType.CUSTOMER) {
            Customer newCustomer = Customer.builder()
                    .user(newUser)
                    .build();

            newUser.setCustomer(newCustomer);
        }

        return userRepository.save(newUser);
    }
}