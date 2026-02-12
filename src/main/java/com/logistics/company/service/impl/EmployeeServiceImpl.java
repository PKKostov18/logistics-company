package com.logistics.company.service.impl;

import com.logistics.company.data.*;
import com.logistics.company.dto.CreateEmployeeRequest;
import com.logistics.company.repository.EmployeeRepository;
import com.logistics.company.repository.OfficeRepository;
import com.logistics.company.repository.RoleRepository;
import com.logistics.company.repository.UserRepository;
import com.logistics.company.service.EmployeeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
   Сервиз за управление на служители.
   Използва репозиторитата за достъп до данните и
   осигурява бизнес логиката, свързана със служителите.
 */

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final OfficeRepository officeRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeServiceImpl(UserRepository userRepository,
                               EmployeeRepository employeeRepository,
                               OfficeRepository officeRepository,
                               RoleRepository roleRepository,
                               PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.officeRepository = officeRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    @Transactional
    public void createEmployee(CreateEmployeeRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }

        Office office = officeRepository.findById(request.getOfficeId())
                .orElseThrow(() -> new EntityNotFoundException("Office not found"));

        RoleType roleType = mapEmployeeTypeToRoleType(request.getEmployeeType());
        Role role = roleRepository.findByName(roleType)
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleType));

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(role)
                .build();

        User savedUser = userRepository.save(user);

        Employee employee = Employee.builder()
                .user(savedUser)
                .office(office)
                .employeeType(request.getEmployeeType())
                .hireDate(LocalDate.now())
                .build();

        employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public void updateEmployee(Long id, String firstName, String lastName, String email, EmployeeType type, Long officeId) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        // Обновяваме User данните
        User user = employee.getUser();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        userRepository.save(user);

        employee.setEmployeeType(type);

        if (officeId != null) {
            Office office = officeRepository.findById(officeId)
                    .orElseThrow(() -> new IllegalArgumentException("Office not found"));
            employee.setOffice(office);
        } else {
            employee.setOffice(null);
        }

        employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        Long userId = employee.getUser().getId();
        employeeRepository.delete(employee);
        userRepository.deleteById(userId);
    }

    private RoleType mapEmployeeTypeToRoleType(EmployeeType employeeType) {
        return switch (employeeType) {
            case OFFICE -> RoleType.OFFICE_EMPLOYEE;
            case COURIER -> RoleType.COURIER;
        };
    }

    @Override
    public Employee getCurrentEmployee(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));

        return employeeRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Employee details not found for user: " + username));
    }
}