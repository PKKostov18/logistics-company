// UserRepository.java
package com.logistics.company.repository;

import com.logistics.company.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// JpaRepository предоставя основните CRUD (Create, Read, Update, Delete) операции
public interface UserRepository extends JpaRepository<User, Integer> {

    // Custom метод за намиране на потребител по username (нужен за проверка при регистрация)
    Optional<User> findByUsername(String username);

    // Custom метод за намиране на потребител по email (нужен за проверка при регистрация)
    Optional<User> findByEmail(String email);

    // Spring Data JPA автоматично генерира имплементацията на тези методи.
}