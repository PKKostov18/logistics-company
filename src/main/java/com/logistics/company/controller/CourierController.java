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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Контролер за куриерската зона и управление на куриери.
 * Позволява на куриерите да виждат своите доставки, да приемат нови пратки и да маркират пратки като доставени.
 */

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
    public String myDeliveries(@RequestParam(required = false) String trackingNumber,
                               Model model,
                               @AuthenticationPrincipal UserDetails userDetails) {

        User courier = userService.findByUsername(userDetails.getUsername());
        List<Package> packages;

        if (trackingNumber != null && !trackingNumber.trim().isEmpty()) {
            Package foundPackage = packageService.getPackageByTrackingNumberForCourier(trackingNumber.trim(), courier);

            if (foundPackage != null) {
                packages = List.of(foundPackage);
            } else {
                packages = List.of();
            }
        } else {
            packages = packageService.getPackagesForCourier(courier);
        }

        model.addAttribute("packages", packages);
        return "courier/deliveries";
    }

    @PostMapping("/deliver/{id}")
    public String deliverPackage(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        packageService.markPackageAsDelivered(id);

        redirectAttributes.addFlashAttribute("successMessage", "Package delivered successfully!");

        return "redirect:/courier/deliveries";
    }

    @GetMapping("/available")
    public String availablePackages(@RequestParam(required = false) String city, Model model) {
        List<Package> packages = packageService.getAvailablePackages(city);

        model.addAttribute("packages", packages);
        model.addAttribute("cityFilter", city);
        return "courier/available";
    }

    @PostMapping("/take/batch")
    public String takeSelectedPackages(@RequestParam(value = "packageIds", required = false) List<Long> packageIds,
                                       @AuthenticationPrincipal UserDetails userDetails,
                                       RedirectAttributes redirectAttributes) {

        if (packageIds == null || packageIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select at least one package.");
            return "redirect:/courier/available";
        }

        User courier = userService.findByUsername(userDetails.getUsername());

        // Обхождаме всички избрани ID-та и ги присвояваме
        for (Long id : packageIds) {
            packageService.assignPackageToCourier(id, courier);
        }

        redirectAttributes.addFlashAttribute("successMessage", "Successfully took " + packageIds.size() + " packages!");
        return "redirect:/courier/deliveries";
    }
}