package com.logistics.company.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

/**
 * Компонент за обработка на успешно влизане в системата.
 * Проверява ролята на потребителя и го пренасочва към съответния Dashboard.
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * Изпълнява се автоматично след успешен логин.
     * @param request текущата заявка
     * @param response текущият отговор (за извършване на редирект)
     * @param authentication съдържа информация за логнатия потребител и неговите права (роли)
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // Извличаме ролите на потребителя в Set от стрингове за лесна проверка
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        // Логика за пренасочване според йерархията на ролите
        if (roles.contains("ROLE_ADMIN")) {
            response.sendRedirect("/admin/dashboard");
        } else if (roles.contains("ROLE_OFFICE_EMPLOYEE")) {
            response.sendRedirect("/office-employee/dashboard");
        } else if (roles.contains("ROLE_COURIER")) {
            response.sendRedirect("/courier/dashboard");
        } else {
            response.sendRedirect("/client/dashboard");
        }
    }
}