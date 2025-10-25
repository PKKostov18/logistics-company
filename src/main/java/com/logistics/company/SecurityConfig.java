package com.logistics.company;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // <-- 1. РАЗРЕШИ ВСИЧКИ ЗАЯВКИ
                )
                .formLogin(form -> form.disable()) // <-- 2. Изключи страницата за вход
                .httpBasic(basic -> basic.disable()) // <-- 3. Изключи Basic Auth (прозореца)
                .csrf(csrf -> csrf.disable()); // <-- 4. Изключи CSRF защитата

        return http.build();
    }
}