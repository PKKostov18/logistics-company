package com.logistics.company.service;

import com.logistics.company.data.*;
import com.logistics.company.repository.CustomerRepository;
import com.logistics.company.repository.RoleRepository;
import com.logistics.company.repository.UserRepository;
import com.logistics.company.dto.RegistrationRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomerRepository customerRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, CustomerRepository customerRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public void registerNewUser(RegistrationRequest registrationRequest) {

        if (userRepository.findByPhoneNumber(registrationRequest.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("Phone number is already registered.");
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

            Optional<Customer> existingCustomer = customerRepository.findByPhoneNumber(registrationRequest.getPhoneNumber());

            if (existingCustomer.isPresent()) {
                Customer customer = existingCustomer.get();

                customer.setName(newUser.getFirstName() + " " + newUser.getLastName());

                customer.setUser(newUser);
                newUser.setCustomer(customer);

            } else {
                Customer newCustomer = Customer.builder()
                        .user(newUser)
                        .name(newUser.getFirstName() + " " + newUser.getLastName())
                        .phoneNumber(newUser.getPhoneNumber())
                        .build();

                newUser.setCustomer(newCustomer);
            }
        }

        userRepository.save(newUser);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
    }
}