package com.logistics.company.mapper;

import com.logistics.company.data.Package;
import com.logistics.company.dto.PackageResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Компонент за преобразуване (Mapping) на данни.
 * Превръща ентита (Package) в DTO (PackageResponse), за да скрие вътрешната структура
 * на базата данни и да улесни визуализацията във frontend-а.
 */

@Component
public class PackageMapper {

    public PackageResponse toResponse(Package pkg) {
        // ползва се @Builder за по-лесно създаване на обектите и по-ясен код
        PackageResponse.PackageResponseBuilder builder = PackageResponse.builder()
                .id(pkg.getId())
                .trackingNumber(pkg.getTrackingNumber())
                .price(pkg.getPrice())
                .status(pkg.getStatus())
                .deliveryType(pkg.getDeliveryType())
                .description(pkg.getDescription())
                .deliveryAddress(pkg.getDeliveryAddress())
                .weightKg(pkg.getWeightKg());

        // Мапинг на Sender с проверка за null стойности,
        // за да се избегне NullPointerException
        if (pkg.getSender() != null) {
            builder.senderName(pkg.getSender().getName());
            if (pkg.getSender().getUser() != null) {
                builder.senderPhone(pkg.getSender().getUser().getPhoneNumber());
            } else {
                builder.senderPhone(pkg.getSender().getPhoneNumber());
            }
        } else {
            builder.senderName("Unknown").senderPhone("-");
        }

        // Мапинг за Receiver
        if (pkg.getReceiver() != null) {
            builder.receiverName(pkg.getReceiver().getName());
            if (pkg.getReceiver().getUser() != null) {
                builder.receiverPhone(pkg.getReceiver().getUser().getPhoneNumber());
            } else {
                builder.receiverPhone(pkg.getReceiver().getPhoneNumber());
            }
        } else {
            builder.receiverName("Unknown").receiverPhone("-");
        }

        // Мапинг за Куриер с проверка за null стойности
        if (pkg.getAssignedCourier() != null && pkg.getAssignedCourier().getUser() != null) {
            builder.courierName(pkg.getAssignedCourier().getUser().getFirstName() + " " +
                    pkg.getAssignedCourier().getUser().getLastName());
        }

        // Мапинг за Офиси с проверка за null стойности
        if (pkg.getDestinationOffice() != null) {
            builder.destinationOffice(pkg.getDestinationOffice().getName());
            builder.destinationOfficeAddress(pkg.getDestinationOffice().getAddress());
        }

        return builder.build();
    }

    // Помощен метод за преобразуване на списък от пратки.
    public List<PackageResponse> toResponseList(List<Package> packages) {
        if (packages == null) return List.of();
        return packages.stream().map(this::toResponse).collect(Collectors.toList());
    }
}