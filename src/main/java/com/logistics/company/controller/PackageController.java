package com.logistics.company.controller;

import com.logistics.company.data.Package;
import com.logistics.company.dto.CreatePackageRequest;
import com.logistics.company.mapper.PackageMapper;
import com.logistics.company.service.OfficeService; // Добавен импорт
import com.logistics.company.service.PackageService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/packages")
public class PackageController {

    private final PackageService packageService;
    private final OfficeService officeService;

    public PackageController(PackageService packageService, OfficeService officeService) {
        this.packageService = packageService;
        this.officeService = officeService;
    }

    @GetMapping
    public String listPackages(Model model, Principal principal) {
        List<Package> packages = packageService.getPackagesForUser(principal.getName());
        model.addAttribute("packages", packages);
        return "packages/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("createPackageRequest", new CreatePackageRequest());
        // ВАЖНО: Подаваме списъка с офиси към HTML-а
        model.addAttribute("offices", officeService.getAllOffices());
        return "packages/create";
    }

    @PostMapping("/create")
    public String createPackage(
            @Valid @ModelAttribute("createPackageRequest") CreatePackageRequest request,
            BindingResult bindingResult,
            Principal principal,
            Model model) {

        if (bindingResult.hasErrors()) {
            // Ако има грешка, трябва пак да подадем офисите, иначе падащото меню ще изчезне
            model.addAttribute("offices", officeService.getAllOffices());
            return "packages/create";
        }

        String employeeUsername = principal != null ? principal.getName() : "system";
        packageService.registerPackage(request, employeeUsername);

        return "redirect:/packages";
    }
}