package com.logistics.company.controller;

import com.logistics.company.service.EmployeeService;
import com.logistics.company.service.OfficeService;
import com.logistics.company.dto.CreateEmployeeRequest;
import com.logistics.company.dto.CreateOfficeRequest;
import com.logistics.company.data.EmployeeType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final OfficeService officeService;
    private final EmployeeService employeeService;

    public AdminController(OfficeService officeService, EmployeeService employeeService) {
        this.officeService = officeService;
        this.employeeService = employeeService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String showDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/offices")
    @PreAuthorize("hasRole('ADMIN')")
    public String manageOffices(Model model) {
        model.addAttribute("offices", officeService.getAllOffices());
        model.addAttribute("officeRequest", new CreateOfficeRequest());
        return "admin/offices";
    }

    @PostMapping("/offices/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveOffice(@ModelAttribute("officeRequest") CreateOfficeRequest request,
                             @RequestParam(required = false) Long id) {
        if (id != null) {
            officeService.updateOffice(id, request);
        } else {
            officeService.createOffice(request);
        }
        return "redirect:/admin/offices";
    }

    @PostMapping("/offices/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteOffice(@PathVariable Long id) {
        officeService.deleteOffice(id);
        return "redirect:/admin/offices";
    }

    @GetMapping("/employees")
    @PreAuthorize("hasRole('ADMIN')")
    public String manageEmployees(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());
        model.addAttribute("offices", officeService.getAllOffices());
        model.addAttribute("employeeTypes", EmployeeType.values());
        model.addAttribute("employeeRequest", new CreateEmployeeRequest());
        return "admin/employees";
    }

    @PostMapping("/employees/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveEmployee(@ModelAttribute("employeeRequest") CreateEmployeeRequest request,
                               @RequestParam(required = false) Long id) {
        if (id != null) {
            employeeService.updateEmployee(id, request);
        } else {
            employeeService.createEmployee(request);
        }
        return "redirect:/admin/employees";
    }

    @PostMapping("/employees/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return "redirect:/admin/employees";
    }

    @GetMapping("/reports")
    @PreAuthorize("hasRole('ADMIN')")
    public String showReports() {
        return "admin/dashboard";
    }
}