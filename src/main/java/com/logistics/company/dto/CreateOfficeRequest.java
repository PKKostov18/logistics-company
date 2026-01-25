package com.logistics.company.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateOfficeRequest {

    @NotBlank(message = "Office name is required")
    private String name;

    @NotBlank(message = "Office address is required")
    private String address;
}
