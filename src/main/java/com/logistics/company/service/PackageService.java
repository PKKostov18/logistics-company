package com.logistics.company.service;

import com.logistics.company.data.DeliveryType;
import com.logistics.company.data.Package;
import com.logistics.company.dto.CreatePackageRequest;

import java.math.BigDecimal;
import java.util.List;

public interface PackageService {
    List<Package> getAllPackages();

    List<Package> getPackagesByUserId(Long userId);

    List<Package> getPackagesForUser(String username);

    Package registerPackage(CreatePackageRequest request, String employeeUsername);

    void updatePackageStatus(Long packageId, String newStatus);

    BigDecimal calculatePrice(double weight, boolean toOffice);

    BigDecimal calculatePrice(double weight, DeliveryType deliveryType);
}