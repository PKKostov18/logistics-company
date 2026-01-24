package com.logistics.company.dto;

import com.logistics.company.data.DeliveryType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data; // Ако ползваш Lombok, ако не - генерирай Getters/Setters

import java.math.BigDecimal;

@Data
public class CreatePackageRequest {

    @NotNull(message = "Sender ID is required")
    private Long senderId;

    @NotNull(message = "Receiver ID is required")
    private Long receiverId;

    @NotNull(message = "Delivery type is required")
    private DeliveryType deliveryType;

    private String deliveryAddress;

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    @DecimalMin(value = "0.1", message = "Minimum weight is 0.1 kg")
    private double weight;

    private String description;
}