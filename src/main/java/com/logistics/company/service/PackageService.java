package com.logistics.company.service;

import com.logistics.company.data.DeliveryType;
import com.logistics.company.data.Package;
import com.logistics.company.data.User;
import com.logistics.company.dto.CreatePackageRequest;

import java.math.BigDecimal;
import java.util.List;

public interface PackageService {

    List<Package> getAllPackages();

    List<Package> getPackagesByUserId(Long userId);

    List<Package> getPackagesForUser(String username);

    List<Package> getPendingPackages();

    void registerPackage(CreatePackageRequest request, String employeeUsername);

    void updatePackageStatus(Long packageId, String newStatus);

    BigDecimal calculatePrice(double weight, boolean toOffice);

    BigDecimal calculatePrice(double weight, DeliveryType deliveryType);

    List<Package> getPackagesForCourier(User courier);
    void markPackageAsDelivered(Long packageId);

    List<Package> getAvailablePackages(String city);
    void assignPackageToCourier(Long packageId, User courier);

    Package getPackageByTrackingNumberForCourier(String trackingNumber, User courier);
    Package findPackageByTrackingNumber(String trackingNumber);
}