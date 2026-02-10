package com.logistics.company.dto;

import com.logistics.company.data.DeliveryType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data // Getters, Setters, ToString, EqualsAndHashCode
public class CreatePackageRequest {

    @NotNull(message = "Sender phone number is required")
    private String senderPhoneNumber;

    private String senderName;

    @NotNull(message = "Receiver phone number is required")
    private String receiverPhoneNumber;

    private String receiverName;

    @NotNull(message = "Delivery type is required")
    private DeliveryType deliveryType;

    private String deliveryAddress;
    private Long officeId;

    @NotNull(message = "Weight is required")
    @Positive
    @DecimalMin(value = "0.1")
    private double weight;

    private String description;
}