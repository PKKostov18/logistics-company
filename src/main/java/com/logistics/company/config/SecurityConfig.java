package com.logistics.company.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Defines the password encoder bean.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the main security filter chain (the "firewall").
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http

                .csrf(csrf -> csrf.disable())


                .authorizeHttpRequests(authz -> authz

                        .requestMatchers(
                                "/",
                                "/login",
                                "/register",
                                "/api/auth/register",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()


                        .anyRequest().authenticated()
                )


                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                )


                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .permitAll()
                );

        return http.build();
    }
}