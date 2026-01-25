package com.logistics.company.service.impl;

import com.logistics.company.data.*;
import com.logistics.company.data.Package;
import com.logistics.company.dto.CreatePackageRequest;
import com.logistics.company.repository.CustomerRepository;
import com.logistics.company.repository.EmployeeRepository;
import com.logistics.company.repository.PackageRepository;
import com.logistics.company.repository.UserRepository;
import com.logistics.company.service.PackageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class PackageServiceImpl implements PackageService {

    private final PackageRepository packageRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;

    public PackageServiceImpl(PackageRepository packageRepository, UserRepository userRepository, CustomerRepository customerRepository, EmployeeRepository employeeRepository) {
        this.packageRepository = packageRepository;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.employeeRepository = employeeRepository;
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
    public List<Package> getPackagesForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        RoleType roleType = user.getRole().getName();

        // ADMIN, OFFICE_EMPLOYEE, COURIER могат да виждат всички пратки
        if (roleType == RoleType.ADMIN || roleType == RoleType.OFFICE_EMPLOYEE || roleType == RoleType.COURIER) {
            return packageRepository.findAll();
        }

        // CUSTOMER вижда само своите пратки (като подател или получател)
        // Ползва се Set, за избегване на дублиране, ако е подател и получател в една и съща пратка
        Set<Package> packageSet = new LinkedHashSet<>();
        packageSet.addAll(packageRepository.findAllBySender_User_Id(user.getId()));
        packageSet.addAll(packageRepository.findAllByReceiver_User_Id(user.getId()));

        return new ArrayList<>(packageSet);
    }

    @Transactional // За да е отворена сесията при запис
    public Package registerPackage(CreatePackageRequest request, String employeeUsername) {
        // намира служителя, който регистрира пратката
        User employeeUser = userRepository.findByUsername(employeeUsername)
                .orElseThrow(() -> new IllegalArgumentException("Employee user not found"));

        Employee employee = employeeRepository.findByUser_Id(employeeUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Employee profile not found"));

        // намира Подателя и Получателя
        Customer sender = customerRepository.findById(request.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));

        Customer receiver = customerRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        // изчислява цената
        BigDecimal price = calculatePrice(request.getWeight(), request.getDeliveryType());

        // създава Entity-то
        Package newPackage = Package.builder()
                .trackingNumber(java.util.UUID.randomUUID().toString().substring(0, 10).toUpperCase()) // Генерираме номер
                .sender(sender)
                .receiver(receiver)
                .registeredBy(employee)
                .weightKg(request.getWeight())
                .deliveryType(request.getDeliveryType())
                .deliveryAddress(request.getDeliveryAddress())
                .price(price)
                .status(com.logistics.company.data.PackageStatus.REGISTERED) // Начален статус
                .build();

        // записва в базата
        return packageRepository.save(newPackage);
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
        // логика за ценообразуване
        BigDecimal basePrice = BigDecimal.valueOf(5.00); // базова цена
        BigDecimal weightCharge = BigDecimal.valueOf(weight * 2.50); // цена на кг

        BigDecimal total = basePrice.add(weightCharge);

        // ако е до адрес, се добавя надценка
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