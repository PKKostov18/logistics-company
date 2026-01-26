package com.logistics.company.service;

import com.logistics.company.data.Employee;
import com.logistics.company.dto.CreateEmployeeRequest;
import java.util.List;

public interface EmployeeService {
    List<Employee> getAllEmployees();
    void createEmployee(CreateEmployeeRequest request);
    void updateEmployee(Long id, CreateEmployeeRequest request);
    void deleteEmployee(Long id);
}