package com.logistics.company.service.impl;

import com.logistics.company.data.*;
import com.logistics.company.data.Package;
import com.logistics.company.dto.CreatePackageRequest;
import com.logistics.company.repository.*;
import com.logistics.company.service.PackageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class PackageServiceImpl implements PackageService {

    private final PackageRepository packageRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final OfficeRepository officeRepository;

    public PackageServiceImpl(PackageRepository packageRepository, UserRepository userRepository, CustomerRepository customerRepository, EmployeeRepository employeeRepository, OfficeRepository officeRepository) {
        this.packageRepository = packageRepository;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.employeeRepository = employeeRepository;
        this.officeRepository = officeRepository;
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
        return new ArrayList<>();
    }

    @Override
    @Transactional
    public Package registerPackage(CreatePackageRequest request, String employeeUsername) {

        // 1. Намиране на служителя
        User employeeUser = userRepository.findByUsername(employeeUsername)
                .orElseThrow(() -> new IllegalArgumentException("Employee user not found"));
        Employee employee = employeeUser.getEmployee();
        if (employee == null) {
            // Опит за намиране чрез репозиторито, ако връзката в User не е заредена
            employee = employeeRepository.findByUser_Id(employeeUser.getId())
                    .orElseThrow(() -> new IllegalArgumentException("User is not an employee"));
        }

        // 2. Обработка на ПОДАТЕЛ (Find or Create)
        Customer sender = getOrCreateCustomer(request.getSenderPhoneNumber(), request.getSenderName());

        // 3. Обработка на ПОЛУЧАТЕЛ (Find or Create)
        Customer receiver = getOrCreateCustomer(request.getReceiverPhoneNumber(), request.getReceiverName());

        // 4. Логика за офиса
        Office destinationOffice = null;
        if (request.getDeliveryType() == DeliveryType.TO_OFFICE) {
            if (request.getOfficeId() == null) throw new IllegalArgumentException("Office is required for TO_OFFICE delivery");
            destinationOffice = officeRepository.findById(request.getOfficeId())
                    .orElseThrow(() -> new IllegalArgumentException("Office not found"));
        }

        // 5. Ценообразуване
        BigDecimal price = calculatePrice(request.getWeight(), request.getDeliveryType());

        // 6. Създаване на пратката
        Package newPackage = Package.builder()
                .trackingNumber(java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .sender(sender)
                .receiver(receiver)
                .registeredBy(employee)
                .destinationOffice(destinationOffice)
                .weightKg(request.getWeight())
                .deliveryType(request.getDeliveryType())
                .deliveryAddress(request.getDeliveryAddress())
                .price(price)
                .status(PackageStatus.REGISTERED)
                .build();

        return packageRepository.save(newPackage);
    }

    // --- ПОМОЩЕН МЕТОД: Намери или Създай Клиент ---
    private Customer getOrCreateCustomer(String phoneNumber, String name) {
        // 1. Търсим дали вече имаме клиент с този телефон в таблица 'customers'
        Optional<Customer> existingCustomer = customerRepository.findByPhoneNumber(phoneNumber);

        if (existingCustomer.isPresent()) {
            return existingCustomer.get();
        }

        // 2. Ако няма клиент, проверяваме дали има регистриран User с този телефон
        // (за да ги свържем автоматично)
        Optional<User> existingUser = userRepository.findByPhoneNumber(phoneNumber);

        Customer newCustomer = new Customer();
        newCustomer.setPhoneNumber(phoneNumber);

        if (existingUser.isPresent()) {
            // Има User -> Свързваме го!
            User user = existingUser.get();
            newCustomer.setUser(user);
            // Ако не е подадено име в рекуеста, ползваме това от профила
            String customerName = (name != null && !name.isEmpty())
                    ? name
                    : user.getFirstName() + " " + user.getLastName();
            newCustomer.setName(customerName);
        } else {
            // Няма User -> Създаваме "Гост" клиент
            newCustomer.setUser(null);
            // Тук името е задължително от формата
            newCustomer.setName(name != null ? name : "Unknown Client");
        }

        return customerRepository.save(newCustomer);
    }

    @Override
    public void updatePackageStatus(Long packageId, String newStatus) {
        Package pkg = packageRepository.findById(packageId)
                .orElseThrow(() -> new IllegalArgumentException("Package not found"));
        try {
            pkg.setStatus(PackageStatus.valueOf(newStatus));
            packageRepository.save(pkg);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + newStatus);
        }
    }

    @Override
    public BigDecimal calculatePrice(double weight, boolean toOffice) {
        return calculatePrice(weight, toOffice ? DeliveryType.TO_OFFICE : DeliveryType.TO_ADDRESS);
    }

    @Override
    public BigDecimal calculatePrice(double weight, DeliveryType deliveryType) {
        BigDecimal basePrice = BigDecimal.valueOf(5.00);
        BigDecimal weightCharge = BigDecimal.valueOf(weight * 2.50);
        BigDecimal total = basePrice.add(weightCharge);

        if (deliveryType == DeliveryType.TO_ADDRESS) {
            total = total.add(BigDecimal.valueOf(10.00));
        }
        return total;
    }
}