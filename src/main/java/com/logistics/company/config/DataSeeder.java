package com.logistics.company.config;

import com.logistics.company.data.Role;
import com.logistics.company.repository.RoleRepository;
import com.logistics.company.data.RoleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    public DataSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        logger.info("Starting data loading (Seeding)...");

        for (RoleType roleType : RoleType.values()) {

            if (roleRepository.findByName(roleType).isEmpty()) {

                Role newRole = new Role(roleType);
                roleRepository.save(newRole);
                logger.info("Role successfully created: {}", roleType.name());

            } else {
                logger.warn("Role {} already exists. Skipping.", roleType.name());
            }
        }

        logger.info("Data loading is complete.");
    }
}