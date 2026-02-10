package com.logistics.company.service;

import com.logistics.company.data.DeliveryType;
import com.logistics.company.data.Package;
import com.logistics.company.data.User;
import com.logistics.company.dto.CreatePackageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PackageService {

    // --- ОСНОВНИ ОПЕРАЦИИ (CRUD) ---
    void registerPackage(CreatePackageRequest request, String employeeUsername);

    void updatePackageStatus(Long packageId, String newStatus);

    Package findPackageByTrackingNumber(String trackingNumber);

    // --- СПРАВКИ И СПИСЪЦИ (Служители и Админи) ---
    List<Package> getAllPackages();

    List<Package> getPendingPackages(); // Всички недоставени

    List<Package> getPackagesByEmployee(Long employeeId); // За админ справка

    // --- КЛИЕНТСКИ СПРАВКИ ---
    List<Package> getPackagesForUser(String username); // "Моите пратки" (общо)

    List<Package> getPackagesSentByClient(Long clientId); // Само изпратени (Админ отчет)

    List<Package> getPackagesReceivedByClient(Long clientId); // Само получени (Админ отчет)

    // --- ФИНАНСОВИ СПРАВКИ ---
    BigDecimal calculateIncome(LocalDate startDate, LocalDate endDate); // Приходи за период

    // --- ЛОГИКА ЗА КУРИЕРИ ---
    List<Package> getPackagesForCourier(User courier); // Пратки на конкретен куриер

    List<Package> getAvailablePackages(String city); // Свободни пратки за взимане

    void assignPackageToCourier(Long packageId, User courier);

    void markPackageAsDelivered(Long packageId);

    Package getPackageByTrackingNumberForCourier(String trackingNumber, User courier);

    // --- ЦЕНООБРАЗУВАНЕ ---
    BigDecimal calculatePrice(double weight, DeliveryType deliveryType);
}