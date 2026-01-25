package com.logistics.company.repository;

import com.logistics.company.data.Office;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OfficeRepository extends JpaRepository<Office, Long> {
    Optional<Office> findByName(String name);
}
