package com.logistics.company.dto;

import com.logistics.company.data.DeliveryType;
import com.logistics.company.data.PackageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO за представяне на информацията за пакет, който ще се изпраща към фронтенда.
 * Предназначен е да съдържа само необходимата информация за показване на пакетите в UI-а.
 * Не включва всички полета от Entity класовете, а само тези, които са релевантни за показване.
 * Също така включва някои "flattened" полета (например senderName вместо sender.user.name),
 * за да улесни достъпа до данните от фронтенда.
 */

@Data // Getters, Setters, ToString, EqualsAndHashCode
@Builder // позволява PackageResponse.builder()
@NoArgsConstructor
@AllArgsConstructor
public class PackageResponse {
    private Long id;
    private String trackingNumber;

    private String senderName;
    private String senderPhone;

    private String receiverName;
    private String receiverPhone;

    private String courierName;

    private String destinationOffice;
    private String destinationOfficeAddress;

    private String deliveryAddress;
    private Double weightKg;
    private BigDecimal price;
    private PackageStatus status;
    private DeliveryType deliveryType;
    private String description;

}