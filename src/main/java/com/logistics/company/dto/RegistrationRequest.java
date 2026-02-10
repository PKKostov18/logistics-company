package com.logistics.company.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data // Getters, Setters, ToString, EqualsAndHashCode
public class RegistrationRequest {

    @NotBlank(message = "Username is required.")
    private String username;

    @NotBlank(message = "First name is required.")
    private String firstName;

    @NotBlank(message = "Last name is required.")
    private String lastName;

    @NotBlank(message = "Email is required.")
    @Email(message = "Invalid email format.")
    private String email;

    private String phoneNumber;

    @NotBlank(message = "Password is required.")
    @Size(min = 6, message = "The password must be at least 6 characters.")
    private String password;

}