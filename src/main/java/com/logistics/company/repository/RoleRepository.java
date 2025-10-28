// RoleRepository.java
package com.logistics.company.repository;

import com.logistics.company.data.Role;
import com.logistics.company.data.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(RoleType name);
}