package com.logistics.company.service.impl;

import com.logistics.company.data.Employee;
import com.logistics.company.data.EmployeeType;
import com.logistics.company.data.Office;
import com.logistics.company.data.Role;
import com.logistics.company.data.RoleType;
import com.logistics.company.data.User;
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
    public void registerEmployee(CreateEmployeeRequest request) {
        // проверка дали потребителското име вече съществува
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }

        // fetch-ва офиса чрез officeId
        Office office = officeRepository.findById(request.getOfficeId())
                .orElseThrow(() -> new EntityNotFoundException("Office not found with id: " + request.getOfficeId()));

        // map-ва EmployeeType към RoleType
        RoleType roleType = mapEmployeeTypeToRoleType(request.getEmployeeType());

        // fetch-ва ролята чрез roleType
        Role role = roleRepository.findByName(roleType)
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleType));

        // създава нов User с енкодната парола
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(role)
                .build();

        User savedUser = userRepository.save(user);

        // създава нов Employee, свързан с User и Office
        Employee employee = Employee.builder()
                .user(savedUser)
                .office(office)
                .employeeType(request.getEmployeeType())
                .hireDate(LocalDate.now())
                .build();

        // запазва Employee в базата
        employeeRepository.save(employee);
    }

    private RoleType mapEmployeeTypeToRoleType(EmployeeType employeeType) {
        return switch (employeeType) {
            case OFFICE -> RoleType.OFFICE_EMPLOYEE;
            case COURIER -> RoleType.COURIER;
        };
    }
}
