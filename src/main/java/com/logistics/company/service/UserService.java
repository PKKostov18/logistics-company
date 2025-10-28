// UserService.java
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

    // Уверете се, че имате UserRepository, подобен на RoleRepository
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerNewUser(RegistrationRequest registrationRequest) {

        // 1. Проверка за съществуващи потребители (по потребителско име или email)
        if (userRepository.findByUsername(registrationRequest.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Потребителското име вече е заето.");
        }
        if (userRepository.findByEmail(registrationRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email адресът вече е регистриран.");
        }

        // 2. Намиране на ролята CUSTOMER по подразбиране
        Role defaultRole = roleRepository.findByName(RoleType.CUSTOMER)
                .orElseThrow(() -> new IllegalStateException("Роля 'CUSTOMER' не е намерена в базата данни."));

        // 3. Кодиране на паролата
        String encodedPassword = passwordEncoder.encode(registrationRequest.getPassword());

        // 4. Създаване на нов User обект
        User newUser = new User(
                registrationRequest.getUsername(),
                encodedPassword, // Използваме кодираната парола
                registrationRequest.getEmail(),
                registrationRequest.getFirstName(),
                registrationRequest.getLastName(),
                registrationRequest.getPhoneNumber(),
                defaultRole // Задаваме ролята CUSTOMER по подразбиране
        );

        // 5. Запазване на потребителя
        return userRepository.save(newUser);
    }
}