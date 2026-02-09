package com.logistics.company.service;

import com.logistics.company.data.Customer;
import java.util.List;

public interface CustomerService {
    List<Customer> getAllCustomers();
    Customer getCustomerById(Long id);
    void updateCustomer(Long id, String name, String phoneNumber);
    void deleteCustomer(Long id);
}