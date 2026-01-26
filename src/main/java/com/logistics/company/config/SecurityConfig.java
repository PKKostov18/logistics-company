package com.logistics.company.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        private final CustomAuthenticationSuccessHandler successHandler;

        public SecurityConfig(CustomAuthenticationSuccessHandler successHandler) {
                this.successHandler = successHandler;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

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
                                        "/images/**")
                                .permitAll()
                                .requestMatchers("/home").authenticated()
                                .requestMatchers(HttpMethod.GET, "/packages").authenticated()
                                .requestMatchers("/packages/create", "/packages/edit/**",
                                        "/packages/delete/**")
                                .hasAnyRole("OFFICE_EMPLOYEE", "COURIER", "ADMIN")
                                .anyRequest().authenticated())
                        .formLogin(form -> form
                                .loginPage("/login")
                                .successHandler(successHandler)
                                .permitAll())
                        .logout(logout -> logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/login?logout")
                                .permitAll());

                return http.build();
        }
}