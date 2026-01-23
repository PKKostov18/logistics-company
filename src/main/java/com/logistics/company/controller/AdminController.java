package com.logistics.company.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String showDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/offices")
    @PreAuthorize("hasRole('ADMIN')")
    public String manageOffices() {
        return "admin/dashboard";
    }

    @GetMapping("/employees")
    @PreAuthorize("hasRole('ADMIN')")
    public String manageEmployees() {
        return "admin/dashboard";
    }

    @GetMapping("/reports")
    @PreAuthorize("hasRole('ADMIN')")
    public String showReports() {
        return "admin/dashboard";
    }
}