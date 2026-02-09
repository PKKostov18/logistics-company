package com.logistics.company.controller;

import com.logistics.company.data.Customer;
import com.logistics.company.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clients")
public class ClientController {

    private final CustomerService customerService;

    public ClientController(CustomerService customerService) {
        this.customerService = customerService;
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