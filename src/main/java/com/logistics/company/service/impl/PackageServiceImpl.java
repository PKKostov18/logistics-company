package com.logistics.company.service.impl;

import com.logistics.company.data.*;
import com.logistics.company.data.Package;
import com.logistics.company.dto.CreatePackageRequest;
import com.logistics.company.repository.*;
import com.logistics.company.service.PackageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
        return packageRepository.findAllBySender_User_Id(userId);
    }

    @Override
    public List<Package> getPendingPackages() {
        // Връщаме всичко, което е РЕГИСТРИРАНО или В ПРОЦЕС НА ДОСТАВКА
        List<Package> registered = packageRepository.findAllByStatus(PackageStatus.REGISTERED);
        List<Package> inTransit = packageRepository.findAllByStatus(PackageStatus.IN_TRANSIT);

        List<Package> allPending = new ArrayList<>(registered);
        allPending.addAll(inTransit);
        return allPending;
    }

    @Override
    public List<Package> getPackagesForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getRole().getName() == RoleType.CUSTOMER) {
            List<Package> sent = packageRepository.findAllBySender_User_Id(user.getId());
            List<Package> received = packageRepository.findAllByReceiver_User_Id(user.getId());

            Set<Package> distinctPackages = new HashSet<>(sent);
            distinctPackages.addAll(received);

            return new ArrayList<>(distinctPackages);
        }

        return packageRepository.findAll();
    }

    @Override
    @Transactional
    public Package registerPackage(CreatePackageRequest request, String employeeUsername) {

        User employeeUser = userRepository.findByUsername(employeeUsername)
                .orElseThrow(() -> new IllegalArgumentException("Employee user not found"));

        Employee employee = employeeUser.getEmployee();
        if (employee == null) {
            employee = employeeRepository.findByUser_Id(employeeUser.getId())
                    .orElseThrow(() -> new IllegalArgumentException("User is not an employee"));
        }

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

        BigDecimal price = calculatePrice(request.getWeight(), request.getDeliveryType());

        Package newPackage = new Package();
        newPackage.setTrackingNumber(java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        newPackage.setSender(sender);
        newPackage.setReceiver(receiver);
        newPackage.setRegisteredBy(employee);
        newPackage.setDestinationOffice(destinationOffice);

        // ПОПРАВКА: Използваме setWeightKg вместо setWeight
        newPackage.setWeightKg(request.getWeight());

        newPackage.setDeliveryType(request.getDeliveryType());
        newPackage.setDeliveryAddress(finalDeliveryAddress);
        newPackage.setPrice(price);
        newPackage.setStatus(PackageStatus.REGISTERED);

        // Ако има поле created_at
        // newPackage.setCreatedAt(java.time.Instant.now());

        return packageRepository.save(newPackage);
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
                // pkg.setReceivedAt(java.time.Instant.now()); // Ако ползваш Instant
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
        Customer newCustomer = getCustomer(phoneNumber, name, existingUser);

        return customerRepository.save(newCustomer);
    }

    private static Customer getCustomer(String phoneNumber, String name, Optional<User> existingUser) {
        Customer newCustomer = new Customer();
        newCustomer.setPhoneNumber(phoneNumber);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            newCustomer.setUser(user);
            String customerName = (name != null && !name.isEmpty())
                    ? name
                    : user.getFirstName() + " " + user.getLastName();
            newCustomer.setName(customerName);
        } else {
            newCustomer.setUser(null);
            newCustomer.setName(name != null && !name.isEmpty() ? name : "Guest Customer");
        }
        return newCustomer;
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