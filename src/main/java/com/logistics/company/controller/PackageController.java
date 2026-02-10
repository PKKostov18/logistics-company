package com.logistics.company.controller;

import com.logistics.company.data.Package;
import com.logistics.company.dto.CreatePackageRequest;
import com.logistics.company.dto.PackageResponse;
import com.logistics.company.mapper.PackageMapper;
import com.logistics.company.service.OfficeService;
import com.logistics.company.service.PackageService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/packages")
public class PackageController {

    private final PackageService packageService;
    private final OfficeService officeService;
    private final PackageMapper packageMapper;

    // Добавя се packageMapper в конструктора
    public PackageController(PackageService packageService,
                             OfficeService officeService,
                             PackageMapper packageMapper) {
        this.packageService = packageService;
        this.officeService = officeService;
        this.packageMapper = packageMapper;
    }

    // Списък на всички пратки (Admin / Employee)
    @GetMapping
    public String listPackages(Model model) {
        List<Package> packages = packageService.getAllPackages();

        List<PackageResponse> responseDTOs = packageMapper.toResponseList(packages);

        model.addAttribute("packages", responseDTOs);
        return "packages/list";
    }

    // Форма за създаване
    @GetMapping("/create")
    @PreAuthorize("hasRole('OFFICE_EMPLOYEE')")
    public String showCreatePackageForm(Model model) {
        model.addAttribute("createPackageRequest", new CreatePackageRequest());
        model.addAttribute("offices", officeService.getAllOffices());
        return "packages/create";
    }

    // Логика за създаване
    @PostMapping("/create")
    @PreAuthorize("hasRole('OFFICE_EMPLOYEE')")
    public String createPackage(
            @Valid @ModelAttribute("createPackageRequest") CreatePackageRequest request,
            BindingResult bindingResult,
            Principal principal,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("offices", officeService.getAllOffices());
            return "packages/create";
        }

        String employeeUsername = principal != null ? principal.getName() : "system";
        packageService.registerPackage(request, employeeUsername);

        return "redirect:/packages";
    }

    // Страница за Pending Shipments
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('OFFICE_EMPLOYEE', 'ADMIN', 'COURIER')")
    public String listPendingPackages(Model model) {
        List<Package> packages = packageService.getPendingPackages();

        // ползва се DTO, за да е консистентно
        List<PackageResponse> responseDTOs = packageMapper.toResponseList(packages);
        model.addAttribute("packages", responseDTOs);

        return "packages/pending";
    }

    // Логика за смяна на статуса
    @PostMapping("/status/update")
    @PreAuthorize("hasAnyRole('OFFICE_EMPLOYEE', 'ADMIN', 'COURIER')")
    public String updatePackageStatus(@RequestParam("id") Long id,
                                      @RequestParam("status") String status) {
        packageService.updatePackageStatus(id, status);
        return "redirect:/packages";
    }
}