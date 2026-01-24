package com.logistics.company.config;

import com.logistics.company.data.Role;
import com.logistics.company.data.RoleType;
import com.logistics.company.data.User;
import com.logistics.company.repository.RoleRepository;
import com.logistics.company.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    public DataSeeder(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        logger.info("Starting data seeding...");

        for (RoleType roleType : RoleType.values()) {
            if (roleRepository.findByName(roleType).isEmpty()) {
                Role newRole = Role.builder().name(roleType).build();
                roleRepository.save(newRole);
                logger.info("Successfully created role: {}", roleType.name());
            }
        }

        seedAdminUser();

        logger.info("Data seeding completed.");
    }

    private void seedAdminUser() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            Role adminRole = roleRepository.findByName(RoleType.ADMIN)
                    .orElseThrow(() -> new IllegalStateException("ADMIN role not found."));

            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("Fuy87181"))
                    .email("admin@logitrace.com")
                    .firstName("System")
                    .lastName("Admin")
                    .phoneNumber("0000000000")
                    .role(adminRole)
                    .build();

            userRepository.save(admin);
            logger.info("Successfully created ADMIN account: username='admin', password='admin123'");
        } else {
            logger.info("Admin account already exists.");
        }
    }
}