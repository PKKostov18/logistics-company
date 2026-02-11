package com.logistics.company;

import com.logistics.company.data.Employee;
import com.logistics.company.data.Office;
import com.logistics.company.data.Package;
import com.logistics.company.data.User;
import com.logistics.company.dto.CreateOfficeRequest;
import com.logistics.company.repository.CompanyRepository;
import com.logistics.company.repository.OfficeRepository;
import com.logistics.company.repository.PackageRepository;
import com.logistics.company.service.impl.OfficeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OfficeServiceImplTest {

    @Mock
    private OfficeRepository officeRepository;

    @Mock
    private PackageRepository packageRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private OfficeServiceImpl officeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllOffices_ShouldReturnAllOffices() {
        Office office1 = Office.builder().id(1L).name("Office 1").build();
        Office office2 = Office.builder().id(2L).name("Office 2").build();

        when(officeRepository.findAll()).thenReturn(List.of(office1, office2));

        var result = officeService.getAllOffices();

        assertEquals(2, result.size());
        assertTrue(result.contains(office1));
        assertTrue(result.contains(office2));
    }

    @Test
    void deleteOffice_ShouldDeleteSuccessfully_WhenNoPackages() {
        User user = User.builder()
                .id(1L)
                .username("Vanko")
                .firstName("Ivan")
                .lastName("Dimov")
                .build();

        Employee employee = Employee.builder()
                .id(1L)
                .user(user)
                .build();

        Set<Employee> employees = new HashSet<>();
        employees.add(employee);

        Office office = Office.builder()
                .id(1L)
                .name("Sofia Office")
                .employees(employees)
                .build();

        when(officeRepository.findById(1L)).thenReturn(Optional.of(office));
        when(packageRepository.findAllByDestinationOffice(office)).thenReturn(Collections.emptyList());

        officeService.deleteOffice(1L);

        assertNull(employee.getOffice());
        verify(officeRepository).deleteById(1L);
    }

    @Test
    void deleteOffice_ShouldThrowException_WhenPackagesExist() {
        Office office = Office.builder().id(1L).name("Sofia Office").build();
        Package pkg = Package.builder().id(1L).destinationOffice(office).build();

        when(officeRepository.findById(1L)).thenReturn(Optional.of(office));
        when(packageRepository.findAllByDestinationOffice(office)).thenReturn(List.of(pkg));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> officeService.deleteOffice(1L));

        assertEquals("Cannot delete office! There are active packages assigned to it.", exception.getMessage());
        verify(officeRepository, never()).deleteById(any());
    }

    @Test
    void deleteOffice_ShouldThrowException_WhenOfficeNotFound() {
        when(officeRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> officeService.deleteOffice(1L));

        assertEquals("Office not found", exception.getMessage());
        verify(officeRepository, never()).deleteById(any());
    }

    @Test
    void createOffice_ShouldSaveOfficeSuccessfully() {
        CreateOfficeRequest request = new CreateOfficeRequest();
        request.setName("New Office");
        request.setAddress("123 Main St");

        when(companyRepository.findAll()).thenReturn(List.of(new com.logistics.company.data.Company()));

        officeService.createOffice(request);

        verify(officeRepository).save(any(Office.class));
    }

    @Test
    void updateOffice_ShouldUpdateExistingOffice() {
        CreateOfficeRequest request = new CreateOfficeRequest();
        request.setName("Updated Office");
        request.setAddress("456 Updated St");

        Office existingOffice = Office.builder().id(1L).name("Old Office").address("Old Address").build();
        when(officeRepository.findById(1L)).thenReturn(Optional.of(existingOffice));

        officeService.updateOffice(1L, request);

        assertEquals("Updated Office", existingOffice.getName());
        assertEquals("456 Updated St", existingOffice.getAddress());
        verify(officeRepository).save(existingOffice);
    }

    @Test
    void updateOffice_ShouldThrowException_WhenOfficeNotFound() {
        CreateOfficeRequest request = new CreateOfficeRequest();
        request.setName("Updated Office");
        request.setAddress("456 Updated St");

        when(officeRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> officeService.updateOffice(1L, request));

        assertEquals("Office not found", exception.getMessage());
        verify(officeRepository, never()).save(any());
    }
}
