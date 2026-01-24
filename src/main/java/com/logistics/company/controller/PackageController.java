package com.logistics.company.controller;

import com.logistics.company.data.Package;
import com.logistics.company.dto.CreatePackageRequest;
import com.logistics.company.mapper.PackageMapper;
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
    private final PackageMapper packageMapper;

    public PackageController(PackageService packageService, PackageMapper packageMapper) {
        this.packageService = packageService;
        this.packageMapper = packageMapper;
    }

    // списък с всички пратки
    @GetMapping
    public String listPackages(Model model) {
        List<Package> packages = packageService.getAllPackages();
        model.addAttribute("packages", packages);
        return "packages/list";
    }

    // форма за създаване на пратка
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("createPackageRequest", new CreatePackageRequest());
        return "packages/create";
    }

    // обработка на формата за създаване на пратка
    @PostMapping("/create")
    public String createPackage(
            @Valid @ModelAttribute("createPackageRequest") CreatePackageRequest request,
            BindingResult bindingResult,
            Principal principal,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "packages/create";
        }

        String employeeUsername = principal != null ? principal.getName() : "system";
        packageService.registerPackage(request, employeeUsername);

        return "redirect:/packages";
    }
}
