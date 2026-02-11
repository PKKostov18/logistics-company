package com.logistics.company.service;

import com.logistics.company.data.Employee;
import com.logistics.company.data.EmployeeType;
import com.logistics.company.dto.CreateEmployeeRequest;
import java.util.List;

public interface EmployeeService {
    List<Employee> getAllEmployees();
    void createEmployee(CreateEmployeeRequest request);
    void deleteEmployee(Long id);
    Employee getCurrentEmployee(String username);
    void updateEmployee(Long id, String firstName, String lastName, String email, EmployeeType type, Long officeId);
}