package com.logistics.company.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Основна конфигурация за сигурността на приложението (Spring Security).
 * Дефинират се правилата за достъп, криптирането на пароли и логин процеса.
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler successHandler;

    public SecurityConfig(CustomAuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
    }

    /**
     * Дефинира бийн за криптиране на пароли.
     * Използва BCrypt алгоритъм, който е индустриален стандарт за сигурност.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
    * Конфигурира филтъра за сигурност (Security Filter Chain).
    * Определя кои URL адреси са публични и кои изискват специфични роли.
    * @param http HttpSecurity обект за настройка на уеб сигурността.
    * @return конфигурираната верига от филтри.
    */

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        // Публични ресурси (достъпни за всички)
                        .requestMatchers(
                                "/",
                                "/login",
                                "/offices",
                                "/error",
                                "/about",
                                "/register",
                                "/api/auth/register",
                                "/css/**",
                                "/js/**",
                                "/images/**")
                        .permitAll()

                        // Основни страници изискващи вход
                        .requestMatchers("/home").authenticated()
                        .requestMatchers(HttpMethod.GET, "/packages").authenticated()

                        // КУРИЕРИ
                        .requestMatchers("/courier/**").hasAnyRole("COURIER", "ADMIN")
                        .requestMatchers("/packages/**").hasAnyRole("OFFICE_EMPLOYEE", "ADMIN", "COURIER")

                        // ПРАТКИ (Create/Edit/Delete)
                        .requestMatchers("/packages/create", "/packages/edit/**",
                                "/packages/delete/**")

                        .hasAnyRole("OFFICE_EMPLOYEE", "COURIER", "ADMIN")

                        // КЛИЕНТИ (Управление на клиенти)
                        .requestMatchers("/clients/**").hasAnyRole("OFFICE_EMPLOYEE", "ADMIN")

                        // Всичко останало изисква аутентикация
                        .anyRequest().authenticated())

                // Настройки на формата за вход
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(successHandler)
                        .permitAll())

                // Настройки за изход
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll());

        return http.build();
    }
}