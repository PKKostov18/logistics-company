package com.logistics.company.controller;

import com.logistics.company.data.Package;
import com.logistics.company.service.OfficeService;
import com.logistics.company.service.PackageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {

    private final PackageService packageService;
    private final OfficeService officeService;

    public PageController(PackageService packageService, OfficeService officeService) {
        this.packageService = packageService;
        this.officeService = officeService;
    }

    @GetMapping("/")
    public String showHomePage(@RequestParam(required = false) String trackingNumber, Model model) {
        if (trackingNumber != null && !trackingNumber.trim().isEmpty()) {
            Package pkg = packageService.findPackageByTrackingNumber(trackingNumber.trim());
            model.addAttribute("searchedPackage", pkg);
            model.addAttribute("trackingNumber", trackingNumber);

            if (pkg == null) {
                model.addAttribute("error", "Package with this tracking number was not found.");
            }
        }
        return "index";
    }

    @GetMapping("/offices")
    public String showOfficesPage(Model model) {
        model.addAttribute("offices", officeService.getAllOffices());
        return "offices";
    }

    @GetMapping("/about")
    public String showAboutPage() {
        return "about";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
}