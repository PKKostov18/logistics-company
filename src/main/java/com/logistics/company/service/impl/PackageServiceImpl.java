package com.logistics.company.service.impl;

import com.logistics.company.data.*;
import com.logistics.company.data.Package;
import com.logistics.company.dto.CreatePackageRequest;
import com.logistics.company.repository.*;
import com.logistics.company.service.PackageService;
import com.logistics.company.service.PricingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class PackageServiceImpl implements PackageService {

    private final PackageRepository packageRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final OfficeRepository officeRepository;
    private final PricingService pricingService;

    public PackageServiceImpl(PackageRepository packageRepository,
                              UserRepository userRepository,
                              CustomerRepository customerRepository,
                              EmployeeRepository employeeRepository,
                              OfficeRepository officeRepository,
                              PricingService pricingService) {
        this.packageRepository = packageRepository;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.employeeRepository = employeeRepository;
        this.officeRepository = officeRepository;
        this.pricingService = pricingService;
    }

    // --- РЕГИСТРАЦИЯ НА ПРАТКА ---
    @Override
    @Transactional
    public void registerPackage(CreatePackageRequest request, String employeeUsername) {
        // намира на служителя, който регистрира
        User employeeUser = userRepository.findByUsername(employeeUsername)
                .orElseThrow(() -> new IllegalArgumentException("Employee user not found"));
        Employee employee = employeeRepository.findByUser_Id(employeeUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("User is not an employee"));

        // обработка на клиенти (подател/получател) - създава ги, ако ги няма
        Customer sender = getOrCreateCustomer(request.getSenderPhoneNumber(), request.getSenderName());
        Customer receiver = getOrCreateCustomer(request.getReceiverPhoneNumber(), request.getReceiverName());

        // обработка на адрес/офис
        Office destinationOffice = null;
        String finalDeliveryAddress = request.getDeliveryAddress();

        if (request.getDeliveryType() == DeliveryType.TO_OFFICE) {
            if (request.getOfficeId() == null) throw new IllegalArgumentException("Office is required for TO_OFFICE delivery");
            destinationOffice = officeRepository.findById(request.getOfficeId())
                    .orElseThrow(() -> new IllegalArgumentException("Office not found"));
            finalDeliveryAddress = null; // ако е до офис, адресът не ни трябва
        } else {
            if (finalDeliveryAddress == null || finalDeliveryAddress.trim().isEmpty()) {
                throw new IllegalArgumentException("Delivery address is required for TO_ADDRESS delivery");
            }
        }

        // смятане на цена (през PricingService)
        BigDecimal price = pricingService.calculatePrice(request.getWeight(), request.getDeliveryType());

        // създаване на обекта
        Package newPackage = Package.builder()
                .trackingNumber(UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .sender(sender)
                .receiver(receiver)
                .registeredBy(employee)
                .destinationOffice(destinationOffice)
                .weightKg(request.getWeight())
                .deliveryType(request.getDeliveryType())
                .deliveryAddress(finalDeliveryAddress)
                .description(request.getDescription())
                .price(price)
                .status(PackageStatus.REGISTERED)
                .createdAt(Instant.now())
                .build();

        packageRepository.save(newPackage);
    }

    // --- ОБНОВЯВАНЕ НА СТАТУС ---
    @Override
    @Transactional
    public void updatePackageStatus(Long packageId, String newStatus) {
        Package pkg = packageRepository.findById(packageId)
                .orElseThrow(() -> new IllegalArgumentException("Package not found"));
        try {
            PackageStatus status = PackageStatus.valueOf(newStatus);
            pkg.setStatus(status);

            // ако е доставена или получена, записва часа
            if (status == PackageStatus.DELIVERED || status == PackageStatus.RECEIVED) {
                pkg.setReceivedAt(Instant.now());
            }
            packageRepository.save(pkg);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + newStatus);
        }
    }

    // --- СПРАВКИ И СПИСЪЦИ ---

    @Override
    public List<Package> getAllPackages() {
        return packageRepository.findAll();
    }

    @Override
    public List<Package> getPendingPackages() {
        return packageRepository.findAllByStatusNot(PackageStatus.DELIVERED);
    }

    @Override
    public List<Package> getPackagesByEmployee(Long employeeId) {
        return packageRepository.findAllByRegisteredBy_User_Id(employeeId);
    }

    // --- КЛИЕНТСКИ МЕТОДИ ---

    @Override
    public List<Package> getPackagesForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getRole().getName() == RoleType.CUSTOMER) {
            return packageRepository.findAllByUserInvolvement(username);
        }
        // ако е служител - вижда всички
        return packageRepository.findAll();
    }

    @Override
    public List<Package> getPackagesSentByClient(Long clientId) {
        return packageRepository.findAllBySender_Id(clientId);
    }

    @Override
    public List<Package> getPackagesReceivedByClient(Long clientId) {
        return packageRepository.findAllByReceiver_Id(clientId);
    }

    // --- ФИНАНСИ (ПРИХОДИ) ---
    @Override
    public BigDecimal calculateIncome(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) return BigDecimal.ZERO;

        // превръща LocalDate (от HTML формата) в Instant (за базата данни)
        Instant start = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant end = endDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant();

        BigDecimal income = packageRepository.calculateIncomeForPeriod(start, end);
        return income != null ? income : BigDecimal.ZERO;
    }

    // --- КУРИЕРСКА ЛОГИКА ---

    @Override
    public List<Package> getPackagesForCourier(User courier) {
        Employee emp = getEmployeeFromUser(courier);
        return packageRepository.findAllByAssignedCourierAndStatusNot(emp, PackageStatus.DELIVERED);
    }

    @Override
    public List<Package> getAvailablePackages(String city) {
        // търси свободни пратки (без куриер), регистрирани, филтрирани по град (ако има такъв)
        if (city != null && !city.trim().isEmpty()) {
            return packageRepository.findAllByAssignedCourierIsNullAndStatusAndDeliveryAddressContainingIgnoreCase(
                    PackageStatus.REGISTERED, city.trim());
        }
        return packageRepository.findAllByAssignedCourierIsNullAndStatus(PackageStatus.REGISTERED);
    }

    @Override
    @Transactional
    public void assignPackageToCourier(Long packageId, User courier) {
        Package pkg = packageRepository.findById(packageId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid package Id: " + packageId));
        Employee emp = getEmployeeFromUser(courier);

        pkg.setAssignedCourier(emp);
        pkg.setStatus(PackageStatus.IN_TRANSIT);
        packageRepository.save(pkg);
    }

    @Override
    public void markPackageAsDelivered(Long packageId) {
        updatePackageStatus(packageId, "DELIVERED");
    }

    @Override
    public Package getPackageByTrackingNumberForCourier(String trackingNumber, User courier) {
        return packageRepository.findByTrackingNumberAndAssignedCourier_User(trackingNumber, courier).orElse(null);
    }

    @Override
    public Package findPackageByTrackingNumber(String trackingNumber) {
        return packageRepository.findByTrackingNumber(trackingNumber).orElse(null);
    }

    @Override
    public BigDecimal calculatePrice(double weight, DeliveryType deliveryType) {
        return pricingService.calculatePrice(weight, deliveryType);
    }

    // --- ПОМОЩНИ МЕТОДИ (Private) ---

    private Employee getEmployeeFromUser(User user) {
        Employee emp = user.getEmployee();
        if (emp == null) {
            emp = employeeRepository.findByUser_Id(user.getId())
                    .orElseThrow(() -> new IllegalArgumentException("User is not an employee"));
        }
        return emp;
    }

    private Customer getOrCreateCustomer(String phoneNumber, String name) {
        // първо търси дали вече съществува такъв клиент
        Optional<Customer> existingCustomer = customerRepository.findByPhoneNumber(phoneNumber);
        if (existingCustomer.isPresent()) {
            return existingCustomer.get();
        }

        // ако не, проверява дали има User с този телефон, за да ги свърже
        Customer newCustomer = new Customer();
        newCustomer.setPhoneNumber(phoneNumber);

        Optional<User> existingUser = userRepository.findByPhoneNumber(phoneNumber);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            newCustomer.setUser(user);
            // използва името от User профила, ако не е подадено друго
            newCustomer.setName((name != null && !name.isEmpty()) ? name : user.getFirstName() + " " + user.getLastName());
        } else {
            // гост клиент (без регистрация)
            newCustomer.setUser(null);
            newCustomer.setName(name != null && !name.isEmpty() ? name : "Guest Customer");
        }
        return customerRepository.save(newCustomer);
    }

    @Override
    public List<Package> findPackagesSentBy(Customer customer) {
        return packageRepository.findAllBySender(customer);
    }

    @Override
    public List<Package> findPackagesReceivedBy(Customer customer) {
        return packageRepository.findAllByReceiver(customer);
    }
}