package com.logistics.company.controller;

import com.logistics.company.data.Package;
import com.logistics.company.data.User;
import com.logistics.company.service.PackageService;
import com.logistics.company.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/courier")
public class CourierController {

    private final PackageService packageService;
    private final UserService userService;

    public CourierController(PackageService packageService, UserService userService) {
        this.packageService = packageService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User courier = userService.findByUsername(userDetails.getUsername());

        List<Package> packages = packageService.getPackagesForCourier(courier);

        model.addAttribute("pendingCount", packages.size());
        model.addAttribute("courierName", courier.getFirstName());

        return "courier/dashboard";
    }

    @GetMapping("/deliveries")
    public String myDeliveries(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User courier = userService.findByUsername(userDetails.getUsername());
        List<Package> packages = packageService.getPackagesForCourier(courier);
        model.addAttribute("packages", packages);
        return "courier/deliveries";
    }

    @PostMapping("/deliver/{id}")
    public String deliverPackage(@PathVariable Long id) {
        packageService.markPackageAsDelivered(id);
        return "redirect:/courier/deliveries";
    }

    @GetMapping("/available")
    public String availablePackages(@RequestParam(required = false) String city, Model model) {
        List<Package> packages = packageService.getAvailablePackages(city);

        model.addAttribute("packages", packages);
        model.addAttribute("cityFilter", city);
        return "courier/available";
    }

    @PostMapping("/take/{id}")
    public String takePackage(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User courier = userService.findByUsername(userDetails.getUsername());

        packageService.assignPackageToCourier(id, courier);

        return "redirect:/courier/deliveries";
    }
}