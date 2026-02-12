package com.logistics.company.controller;

import com.logistics.company.service.OfficeService;
import com.logistics.company.service.PackageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Контролер за общите страници на уеб приложението.
 * Позволява на потребителите да виждат началната страница, страницата с офиси,
 * информация за компанията, както и формите за регистрация и вход.
 */

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
        ClientController.searchPackage(trackingNumber, model, packageService);
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