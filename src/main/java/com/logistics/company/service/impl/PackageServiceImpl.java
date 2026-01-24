package com.logistics.company.service.impl;

import com.logistics.company.data.DeliveryType;
import com.logistics.company.data.Package;
import com.logistics.company.dto.CreatePackageRequest;
import com.logistics.company.repository.PackageRepository;
import com.logistics.company.service.PackageService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class PackageServiceImpl implements PackageService {

    private final PackageRepository packageRepository;
    // TODO: да се инжектират CustomerRepository, OfficeRepository и т.н.

    public PackageServiceImpl(PackageRepository packageRepository) {
        this.packageRepository = packageRepository;
    }

    @Override
    public List<Package> getAllPackages() {
        return packageRepository.findAll();
    }

    @Override
    public List<Package> getPackagesByUserId(Long userId) {
        return List.of();
    }

    @Override
    public Package registerPackage(CreatePackageRequest request, String employeeUsername) {
        return null;
    }

    @Override
    public void updatePackageStatus(Long packageId, String newStatus) {

    }

    @Override
    public BigDecimal calculatePrice(double weight, boolean toOffice) {
        return null;
    }

    @Override
    public BigDecimal calculatePrice(double weight, DeliveryType deliveryType) {
        // Примерна логика за ценообразуване
        BigDecimal basePrice = BigDecimal.valueOf(5.00); // Базова цена
        BigDecimal weightCharge = BigDecimal.valueOf(weight * 2.50); // Цена на кг

        BigDecimal total = basePrice.add(weightCharge);

        // Ако е до адрес, добавяме надценка (по-скъпо)
        if (deliveryType == DeliveryType.TO_ADDRESS) {
            total = total.add(BigDecimal.valueOf(10.00));
        }

        return total;
    }

    // TODO: да се добави и логиката за registerPackage, където:
    // 1. Взима подател, получател от базата.
    // 2. Изчислява цената чрез calculatePrice().
    // 3. Запазва в packageRepository.
}