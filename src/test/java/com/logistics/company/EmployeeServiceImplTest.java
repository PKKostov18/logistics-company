package com.logistics.company;

import com.logistics.company.data.*;
import com.logistics.company.dto.CreateEmployeeRequest;
import com.logistics.company.repository.EmployeeRepository;
import com.logistics.company.repository.OfficeRepository;
import com.logistics.company.repository.RoleRepository;
import com.logistics.company.repository.UserRepository;
import com.logistics.company.service.impl.EmployeeServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private OfficeRepository officeRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private EmployeeServiceImpl employeeService;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeServiceImpl(
                userRepository,
                employeeRepository,
                officeRepository,
                roleRepository,
                passwordEncoder
        );
    }

    @Test
    void getAllEmployees_ShouldReturnList() {

        Employee e1 = Employee.builder().id(1L).build();
        Employee e2 = Employee.builder().id(2L).build();

        when(employeeRepository.findAll()).thenReturn(List.of(e1, e2));

        List<Employee> result = employeeService.getAllEmployees();

        assertEquals(2, result.size());
    }

    @Test
    void createEmployee_ShouldCreateEmployeeSuccessfully() {

        CreateEmployeeRequest request = new CreateEmployeeRequest();
        request.setUsername("Vanko");
        request.setPassword("pass");
        request.setEmail("ivan@email.com");
        request.setFirstName("Ivan");
        request.setLastName("Dimov");
        request.setEmployeeType(EmployeeType.OFFICE);
        request.setOfficeId(1L);

        Office office = Office.builder().id(1L).build();

        Role role = Role.builder()
                .name(RoleType.OFFICE_EMPLOYEE)
                .build();

        User savedUser = User.builder()
                .id(1L)
                .username("Vanko")
                .firstName("Ivan")
                .lastName("Dimov")
                .build();

        when(userRepository.existsByUsername("Vanko")).thenReturn(false);
        when(officeRepository.findById(1L)).thenReturn(Optional.of(office));
        when(roleRepository.findByName(RoleType.OFFICE_EMPLOYEE)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        employeeService.createEmployee(request);

        verify(userRepository).save(any(User.class));
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void createEmployee_ShouldThrowException_WhenUsernameExists() {

        CreateEmployeeRequest request = new CreateEmployeeRequest();
        request.setUsername("Vanko");

        when(userRepository.existsByUsername("Vanko")).thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.createEmployee(request)
        );
    }

    @Test
    void updateEmployee_ShouldUpdateSuccessfully_WithOffice() {

        User user = User.builder()
                .id(1L)
                .username("Vanko")
                .firstName("Ivan")
                .lastName("Dimov")
                .build();

        Office office = Office.builder().id(2L).build();

        Employee employee = Employee.builder()
                .id(1L)
                .user(user)
                .build();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(officeRepository.findById(2L)).thenReturn(Optional.of(office));

        employeeService.updateEmployee(
                1L,
                "Ivan",
                "Dimov",
                "ivan.dimov@email.com",
                EmployeeType.COURIER,
                2L
        );

        assertEquals("Ivan", user.getFirstName());
        assertEquals("Dimov", user.getLastName());
        assertEquals("ivan.dimov@email.com", user.getEmail());
        assertEquals(EmployeeType.COURIER, employee.getEmployeeType());

        verify(userRepository).save(user);
        verify(employeeRepository).save(employee);
    }

    @Test
    void deleteEmployee_ShouldDeleteEmployeeAndUser() {

        User user = User.builder()
                .id(5L)
                .username("Vanko")
                .firstName("Ivan")
                .lastName("Dimov")
                .build();

        Employee employee = Employee.builder()
                .id(1L)
                .user(user)
                .build();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        employeeService.deleteEmployee(1L);

        verify(employeeRepository).delete(employee);
        verify(userRepository).deleteById(5L);
    }

    @Test
    void getCurrentEmployee_ShouldReturnEmployee() {

        User user = User.builder()
                .id(1L)
                .username("Vanko")
                .firstName("Ivan")
                .lastName("Dimov")
                .build();

        Employee employee = Employee.builder()
                .id(10L)
                .user(user)
                .build();

        when(userRepository.findByUsername("Vanko")).thenReturn(Optional.of(user));
        when(employeeRepository.findByUser_Id(1L)).thenReturn(Optional.of(employee));

        Employee result = employeeService.getCurrentEmployee("Vanko");

        assertEquals(10L, result.getId());
        assertEquals("Vanko", result.getUser().getUsername());
    }
}
