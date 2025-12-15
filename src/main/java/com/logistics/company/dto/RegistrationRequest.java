// RegistrationRequest.java
package com.logistics.company.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegistrationRequest {

    @NotBlank(message = "Username е задължително.")
    private String username;

    @NotBlank(message = "Името е задължително.")
    private String firstName;

    @NotBlank(message = "Фамилията е задължителна.")
    private String lastName;

    @NotBlank(message = "Email е задължителен.")
    @Email(message = "Невалиден email формат.")
    private String email;

    private String phoneNumber;

    @NotBlank(message = "Паролата е задължителна.")
    @Size(min = 6, message = "Паролата трябва да е поне 6 символа.")
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}