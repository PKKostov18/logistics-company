package com.logistics.company.controller;

import com.logistics.company.data.Customer;
import com.logistics.company.data.User;
import com.logistics.company.service.CustomerService;
import com.logistics.company.service.PackageService;
import com.logistics.company.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.logistics.company.data.Package;

import java.util.List;

@Controller
@RequestMapping("/client")
public class ClientController {

    private final CustomerService customerService;
    private final PackageService packageService;
    private final UserService userService;

    public ClientController(CustomerService customerService, PackageService packageService, UserService userService) {
        this.customerService = customerService;
        this.packageService = packageService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) String trackingNumber,
                            @AuthenticationPrincipal UserDetails userDetails,
                            Model model) {

        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("user", user);

        searchPackage(trackingNumber, model, packageService);

        return "clients/dashboard";
    }

    static void searchPackage(@RequestParam(required = false) String trackingNumber, Model model, PackageService packageService) {
        if (trackingNumber != null && !trackingNumber.trim().isEmpty()) {
            Package pkg = packageService.findPackageByTrackingNumber(trackingNumber.trim());
            model.addAttribute("searchedPackage", pkg);
            model.addAttribute("trackingNumber", trackingNumber);

            if (pkg == null) {
                model.addAttribute("error", "Package with this tracking number was not found.");
            }
        }
    }

    @GetMapping("/outgoing")
    public String outgoingPackages(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        Customer customer = user.getCustomer();

        if (customer != null) {
            List<Package> packages = packageService.findPackagesSentBy(customer);
            model.addAttribute("packages", packages);
        } else {
            model.addAttribute("packages", List.of());
        }

        model.addAttribute("pageTitle", "Outgoing Packages");
        model.addAttribute("pageDesc", "Shipments you have sent to others.");
        return "clients/package-list";
    }

    @GetMapping("/incoming")
    public String incomingPackages(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        Customer customer = user.getCustomer();

        if (customer != null) {
            List<Package> packages = packageService.findPackagesReceivedBy(customer);
            model.addAttribute("packages", packages);
        } else {
            model.addAttribute("packages", List.of());
        }

        model.addAttribute("pageTitle", "Incoming Packages");
        model.addAttribute("pageDesc", "Shipments sent to you.");
        return "clients/package-list";
    }

    @GetMapping
    public String listClients(Model model) {
        model.addAttribute("clients", customerService.getAllCustomers());
        return "clients/list";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Customer customer = customerService.getCustomerById(id);
        model.addAttribute("client", customer);
        return "clients/edit";
    }

    @PostMapping("/update/{id}")
    public String updateClient(@PathVariable Long id,
                               @RequestParam String name,
                               @RequestParam String phoneNumber) {
        customerService.updateCustomer(id, name, phoneNumber);
        return "redirect:/clients";
    }

}