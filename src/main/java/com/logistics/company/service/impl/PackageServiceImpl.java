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

    @Override
    public List<Package> getAllPackages() {
        return packageRepository.findAll();
    }

    @Override
    public List<Package> getPackagesByUserId(Long userId) {
        return packageRepository.findAllBySender_User_Id(userId);
    }

    @Override
    public List<Package> getPackagesForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getRole().getName() == RoleType.CUSTOMER) {
            // новата оптимизирана заявка в Repository
            return packageRepository.findAllByUserInvolvement(username);
        }

        // За служители връща всички (оптимизирано от findAll)
        return packageRepository.findAll();
    }

    @Override
    public List<Package> getPendingPackages() {
        return packageRepository.findAllByStatusNot(PackageStatus.DELIVERED);
    }

    @Override
    @Transactional
    public void registerPackage(CreatePackageRequest request, String employeeUsername) {
        User employeeUser = userRepository.findByUsername(employeeUsername)
                .orElseThrow(() -> new IllegalArgumentException("Employee user not found"));

        Employee employee = employeeRepository.findByUser_Id(employeeUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("User is not an employee"));

        Customer sender = getOrCreateCustomer(request.getSenderPhoneNumber(), request.getSenderName());
        Customer receiver = getOrCreateCustomer(request.getReceiverPhoneNumber(), request.getReceiverName());

        Office destinationOffice = null;
        String finalDeliveryAddress = request.getDeliveryAddress();

        if (request.getDeliveryType() == DeliveryType.TO_OFFICE) {
            if (request.getOfficeId() == null) {
                throw new IllegalArgumentException("Office is required for TO_OFFICE delivery");
            }
            destinationOffice = officeRepository.findById(request.getOfficeId())
                    .orElseThrow(() -> new IllegalArgumentException("Office not found"));
            finalDeliveryAddress = null;
        } else {
            if (finalDeliveryAddress == null || finalDeliveryAddress.trim().isEmpty()) {
                throw new IllegalArgumentException("Delivery address is required for TO_ADDRESS delivery");
            }
        }

        BigDecimal price = pricingService.calculatePrice(request.getWeight(), request.getDeliveryType());

        Package newPackage = new Package();
        newPackage.setTrackingNumber(java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        newPackage.setSender(sender);
        newPackage.setReceiver(receiver);
        newPackage.setRegisteredBy(employee);
        newPackage.setDestinationOffice(destinationOffice);
        newPackage.setWeightKg(request.getWeight());
        newPackage.setDeliveryType(request.getDeliveryType());
        newPackage.setDeliveryAddress(finalDeliveryAddress);
        newPackage.setPrice(price);
        newPackage.setStatus(PackageStatus.REGISTERED);
        newPackage.setCreatedAt(Instant.now());

        packageRepository.save(newPackage);
    }

    @Override
    @Transactional
    public void updatePackageStatus(Long packageId, String newStatus) {
        Package pkg = packageRepository.findById(packageId)
                .orElseThrow(() -> new IllegalArgumentException("Package not found"));
        try {
            PackageStatus status = PackageStatus.valueOf(newStatus);
            pkg.setStatus(status);

            if (status == PackageStatus.DELIVERED || status == PackageStatus.RECEIVED) {
                pkg.setReceivedAt(Instant.now());
            }
            packageRepository.save(pkg);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + newStatus);
        }
    }

    private Customer getOrCreateCustomer(String phoneNumber, String name) {
        Optional<Customer> existingCustomer = customerRepository.findByPhoneNumber(phoneNumber);
        if (existingCustomer.isPresent()) {
            return existingCustomer.get();
        }
        Optional<User> existingUser = userRepository.findByPhoneNumber(phoneNumber);
        Customer newCustomer = new Customer();
        newCustomer.setPhoneNumber(phoneNumber);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            newCustomer.setUser(user);
            newCustomer.setName((name != null && !name.isEmpty()) ? name : user.getFirstName() + " " + user.getLastName());
        } else {
            newCustomer.setUser(null);
            newCustomer.setName(name != null && !name.isEmpty() ? name : "Guest Customer");
        }
        return customerRepository.save(newCustomer);
    }

    @Override
    public BigDecimal calculatePrice(double weight, boolean toOffice) {
        return pricingService.calculatePrice(weight, toOffice ? DeliveryType.TO_OFFICE : DeliveryType.TO_ADDRESS);
    }

    @Override
    public BigDecimal calculatePrice(double weight, DeliveryType deliveryType) {
        return pricingService.calculatePrice(weight, deliveryType);
    }

    @Override
    public List<Package> getPackagesForCourier(User courier) {
        Employee emp = courier.getEmployee();
        if(emp == null) { // Fallback, ако User обекта няма закачен Employee (lazy loading проблем)
            emp = employeeRepository.findByUser_Id(courier.getId()).orElse(null);
        }
        return packageRepository.findAllByAssignedCourierAndStatusNot(emp, PackageStatus.DELIVERED);
    }

    @Override
    public void markPackageAsDelivered(Long packageId) {
        Package pkg = packageRepository.findById(packageId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid package Id:" + packageId));
        pkg.setStatus(PackageStatus.DELIVERED);
        pkg.setReceivedAt(Instant.now());
        packageRepository.save(pkg);
    }

    @Override
    public List<Package> getAvailablePackages(String city) {
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

        Employee emp = courier.getEmployee();
        if(emp == null) {
            emp = employeeRepository.findByUser_Id(courier.getId()).orElseThrow();
        }

        pkg.setAssignedCourier(emp);
        pkg.setStatus(PackageStatus.IN_TRANSIT);
        packageRepository.save(pkg);
    }

    @Override
    public Package getPackageByTrackingNumberForCourier(String trackingNumber, User courier) {
        return packageRepository.findByTrackingNumberAndAssignedCourier_User(trackingNumber, courier).orElse(null);
    }

    @Override
    public Package findPackageByTrackingNumber(String trackingNumber) {
        return packageRepository.findByTrackingNumber(trackingNumber).orElse(null);
    }
}