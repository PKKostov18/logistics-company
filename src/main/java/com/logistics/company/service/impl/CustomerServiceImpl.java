package com.logistics.company.service.impl;

import com.logistics.company.data.Customer;
import com.logistics.company.repository.CustomerRepository;
import com.logistics.company.service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + id));
    }

    @Override
    @Transactional
    public void updateCustomer(Long id, String name, String phoneNumber) {
        Customer customer = getCustomerById(id);

        customer.setName(name);
        customer.setPhoneNumber(phoneNumber);

        customerRepository.save(customer);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }
}