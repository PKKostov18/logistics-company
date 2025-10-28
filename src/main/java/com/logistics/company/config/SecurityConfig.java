// SecurityConfig.java
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
                // Disable CSRF protection for simplicity, especially for API endpoints
                .csrf(csrf -> csrf.disable())

                // Define authorization rules
                .authorizeHttpRequests(authz -> authz
                        // Allow public access to these specific URLs
                        .requestMatchers(
                                "/",
                                "/login",
                                "/register",
                                "/api/auth/register",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()

                        // All other requests must be authenticated
                        .anyRequest().authenticated()
                )

                // Configure form-based login
                .formLogin(form -> form
                        .loginPage("/login")    // Use our custom login page at /login
                        .permitAll()          // The login page must be public
                )

                // Configure logout
                .logout(logout -> logout
                        .logoutUrl("/logout")   // The URL to trigger logout
                        .permitAll()          // The logout URL must be public
                );

        return http.build();
    }
}