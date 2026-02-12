package com.logistics.company.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO за създаване на нов офис в системата.
 * Използва се от администратори или служители при добавяне на нова локация.
 */

@Data // Getters, Setters, ToString, EqualsAndHashCode
public class CreateOfficeRequest {

    @NotBlank(message = "Office name is required")
    private String name;

    @NotBlank(message = "Office address is required")
    private String address;
}
