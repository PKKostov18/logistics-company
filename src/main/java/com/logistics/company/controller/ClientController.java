package com.logistics.company.controller;

import com.logistics.company.data.Customer;
import com.logistics.company.data.User;
import com.logistics.company.service.CustomerService;
import com.logistics.company.service.PackageService;
import com.logistics.company.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.logistics.company.data.Package;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * Контролер за клиентската зона и управление на клиенти.
 * Позволява на клиентите да виждат своите пратки и да управляват профила си,
 * както и на администраторите и офис служителите да управляват клиентите.
 */

@RequestMapping("/client")
@Controller
public class ClientController {

    private final CustomerService customerService;
    private final PackageService packageService;
    private final UserService userService;

    public ClientController(CustomerService customerService, PackageService packageService, UserService userService) {
        this.customerService = customerService;
        this.packageService = packageService;
        this.userService = userService;
    }

    // --- КЛИЕНТСКА ЗОНА (DASHBOARD) ---
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

    // --- АДМИНИСТРАТИВНА ЧАСТ (Управление на клиенти) ---
    @GetMapping
    public String listClients(Model model) {
        model.addAttribute("clients", customerService.getAllCustomers());
        return "clients/list";
    }

    @PostMapping("/update/{id}")
    @ResponseBody
    public String updateClient(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            String name = payload.get("name");
            String phoneNumber = payload.get("phoneNumber");
            String username = payload.get("username");

            customerService.updateCustomer(id, name, phoneNumber, username);

        } catch (Exception e) {
            return ("Error updating client: " + e.getMessage());
        }
        return "redirect:/clients";
    }

    // DELETE
    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OFFICE_EMPLOYEE')")
    public String deleteClient(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return "redirect:/client";
    }

    @PostMapping("/packages/receive/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String confirmReceipt(@PathVariable Long id, Principal principal) {
        packageService.markPackageAsReceived(id, principal.getName());

        return "redirect:/client/incoming";
    }

}