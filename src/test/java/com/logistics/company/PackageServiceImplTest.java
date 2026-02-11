package com.logistics.company;

import com.logistics.company.data.*;
import com.logistics.company.data.Package;
import com.logistics.company.dto.CreatePackageRequest;
import com.logistics.company.repository.*;
import com.logistics.company.service.PricingService;
import com.logistics.company.service.impl.PackageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PackageServiceImplTest {

    @Mock
    private PackageRepository packageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private OfficeRepository officeRepository;

    @Mock
    private PricingService pricingService;

    @InjectMocks
    private PackageServiceImpl packageService;

    private User employeeUser;
    private Employee employee;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employeeUser = User.builder()
                .id(1L)
                .username("Vanko")
                .firstName("Ivan")
                .lastName("Dimov")
                .build();

        employee = Employee.builder()
                .id(1L)
                .user(employeeUser)
                .build();

        employeeUser.setEmployee(employee);
    }

    @Test
    void registerPackage_ShouldSavePackageSuccessfully() {
        CreatePackageRequest request = new CreatePackageRequest();
        request.setSenderName("Sender Name");
        request.setSenderPhoneNumber("111222333");
        request.setReceiverName("Receiver Name");
        request.setReceiverPhoneNumber("444555666");
        request.setWeight(2.5);
        request.setDeliveryType(DeliveryType.TO_ADDRESS);
        request.setDeliveryAddress("Some Street 1");

        when(userRepository.findByUsername("Vanko")).thenReturn(Optional.of(employeeUser));
        when(employeeRepository.findByUser_Id(1L)).thenReturn(Optional.of(employee));
        when(customerRepository.findByPhoneNumber(anyString())).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenAnswer(i -> i.getArgument(0));
        when(pricingService.calculatePrice(2.5, DeliveryType.TO_ADDRESS)).thenReturn(BigDecimal.valueOf(10));

        packageService.registerPackage(request, "Vanko");

        verify(packageRepository, times(1)).save(any(Package.class));
    }

    @Test
    void updatePackageStatus_ShouldUpdateStatusSuccessfully() {
        Package pkg = Package.builder()
                .id(1L)
                .status(PackageStatus.REGISTERED)
                .build();

        when(packageRepository.findById(1L)).thenReturn(Optional.of(pkg));

        packageService.updatePackageStatus(1L, "DELIVERED");

        assertEquals(PackageStatus.DELIVERED, pkg.getStatus());
        assertNotNull(pkg.getReceivedAt());
        verify(packageRepository, times(1)).save(pkg);
    }

    @Test
    void updatePackageStatus_ShouldThrowForInvalidStatus() {
        Package pkg = Package.builder().id(1L).build();
        when(packageRepository.findById(1L)).thenReturn(Optional.of(pkg));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> packageService.updatePackageStatus(1L, "INVALID_STATUS"));

        assertEquals("Invalid status: INVALID_STATUS", exception.getMessage());
    }

    @Test
    void calculateIncome_ShouldReturnZero_WhenNoPackages() {
        when(packageRepository.calculateIncomeForPeriod(any(), any())).thenReturn(null);
        BigDecimal income = packageService.calculateIncome(LocalDate.now(), LocalDate.now());
        assertEquals(BigDecimal.ZERO, income);
    }
}
