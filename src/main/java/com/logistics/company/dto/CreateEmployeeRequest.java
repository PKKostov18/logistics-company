package com.logistics.company.dto;

import com.logistics.company.data.EmployeeType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO за създаване на нов служител заедно с потребителските данни
 * Използва се при POST заявка за създаване на служител и съдържа всички полета
 * за създаване както на User, така и на Employee обектите в базата данни
 */

@Data // Getters, Setters, ToString, EqualsAndHashCode
public class CreateEmployeeRequest {

    // полета за User
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    // Employee полета
    @NotNull(message = "Office ID is required")
    private Long officeId;

    @NotNull(message = "Employee type is required")
    private EmployeeType employeeType;
}
