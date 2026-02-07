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
    public void registerNewUser(RegistrationRequest registrationRequest) {

        if (userRepository.findByPhoneNumber(registrationRequest.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("Phone number is already registered.");
        }

        // Запазваме проверката за имейл (добра практика е да я има)
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

        // Тук логиката ти е правилна - ако е Customer, създаваме и записа в другата таблица
        if (defaultRole.getName() == RoleType.CUSTOMER) {
            Customer newCustomer = Customer.builder()
                    .user(newUser)
                    // Може да добавиш и името тук, за да избегнеш NULL грешката от предишния въпрос
                    .name(newUser.getFirstName() + " " + newUser.getLastName())
                    .phoneNumber(newUser.getPhoneNumber())
                    .build();

            newUser.setCustomer(newCustomer);
        }

        userRepository.save(newUser);
    }
}