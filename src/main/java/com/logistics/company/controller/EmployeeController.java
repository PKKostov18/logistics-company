package com.logistics.company.controller;

import com.logistics.company.data.Employee;
import com.logistics.company.data.Office;
import com.logistics.company.service.EmployeeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Контролер за офис служителите.
 * Позволява им да виждат информация за офиса, в който работят
 * и да управляват пратките, свързани с този офис.
 */

@Controller
@RequestMapping("/office-employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('OFFICE_EMPLOYEE')")
    public String showDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Employee employee = employeeService.getCurrentEmployee(userDetails.getUsername());

        Office office = employee.getOffice();
        model.addAttribute("office", office);

        return "office_employee/dashboard";
    }
}