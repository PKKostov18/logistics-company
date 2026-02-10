package com.logistics.company.controller;

import com.logistics.company.data.Package;
import com.logistics.company.dto.PackageResponse;
import com.logistics.company.mapper.PackageMapper;
import com.logistics.company.service.CustomerService;
import com.logistics.company.service.EmployeeService;
import com.logistics.company.service.OfficeService;
import com.logistics.company.dto.CreateEmployeeRequest;
import com.logistics.company.dto.CreateOfficeRequest;
import com.logistics.company.data.EmployeeType;
import com.logistics.company.service.PackageService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final OfficeService officeService;
    private final EmployeeService employeeService;
    private final PackageService packageService;
    private final PackageMapper packageMapper;
    private final CustomerService customerService;

    public AdminController(OfficeService officeService, EmployeeService employeeService, PackageService packageService,
                           PackageMapper packageMapper, CustomerService customerService) {
        this.officeService = officeService;
        this.employeeService = employeeService;
        this.packageService = packageService;
        this.packageMapper = packageMapper;
        this.customerService = customerService;
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
        model.addAttribute("newOffice", new CreateOfficeRequest());
        return "admin/offices";
    }

    @PostMapping("/offices/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveOffice(@Valid @ModelAttribute("officeRequest") CreateOfficeRequest request,
                             BindingResult bindingResult,
                             // взима резултата от валидацията
                             Model model) {

        // проверка за грешки
        if (bindingResult.hasErrors()) {
            // връща същата страница, за да се покажат грешките
            // трябва да зареди отново списъците, които страницата очаква
            model.addAttribute("offices", officeService.getAllOffices());
            model.addAttribute("employees", employeeService.getAllEmployees());
            return "admin/offices";
        }

        officeService.createOffice(request);
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
    public String saveEmployee(@Valid @ModelAttribute("employeeRequest") CreateEmployeeRequest request,
                               BindingResult bindingResult,
                               Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("employees", employeeService.getAllEmployees());
            model.addAttribute("offices", officeService.getAllOffices());
            // връща изгледа, където е формата за служители
            return "admin/employees";
        }

        employeeService.createEmployee(request);
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
    public String showReports(Model model,
                              @RequestParam(required = false) String type,
                              @RequestParam(required = false) Long employeeId,
                              @RequestParam(required = false) Long clientId,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // Зареждане на данните за падащите менюта
        model.addAttribute("employees", employeeService.getAllEmployees());
        model.addAttribute("clients", customerService.getAllCustomers());

        // Справка за приходите
        if (startDate != null && endDate != null) {
            BigDecimal income = packageService.calculateIncome(startDate, endDate);
            model.addAttribute("incomeResult", income);
            model.addAttribute("periodStart", startDate);
            model.addAttribute("periodEnd", endDate);
        }

        // Логика за списъците с пратки
        List<Package> packages = new ArrayList<>();
        String reportTitle = "Select a report type to view results:";

        if ("all".equals(type)) {
            packages = packageService.getAllPackages(); // Изискване c
            reportTitle = "All Registered Packages";
        } else if ("pending".equals(type)) {
            packages = packageService.getPendingPackages(); // Изискване e
            reportTitle = "Pending Shipments (Not Delivered)";
        } else if ("by_employee".equals(type) && employeeId != null) {
            packages = packageService.getPackagesByEmployee(employeeId); // Изискване d
            reportTitle = "Packages Registered by Employee #" + employeeId;
        } else if ("sent_by_client".equals(type) && clientId != null) {
            packages = packageService.getPackagesSentByClient(clientId); // Изискване f
            reportTitle = "Packages Sent by Client #" + clientId;
        } else if ("received_by_client".equals(type) && clientId != null) {
            packages = packageService.getPackagesReceivedByClient(clientId); // Изискване g
            reportTitle = "Packages Received by Client #" + clientId;
        }

        // Превръщане в DTO за визуализацията
        List<PackageResponse> responseDTOs = packageMapper.toResponseList(packages);

        model.addAttribute("packages", responseDTOs);
        model.addAttribute("reportTitle", reportTitle);
        model.addAttribute("selectedType", type);

        return "admin/reports";
    }
}