package com.logistics.company.service.impl;

import com.logistics.company.data.Customer;
import com.logistics.company.data.User;
import com.logistics.company.repository.CustomerRepository;
import com.logistics.company.repository.PackageRepository;
import com.logistics.company.repository.UserRepository;
import com.logistics.company.service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.logistics.company.data.Package;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final PackageRepository packageRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository, UserRepository userRepository, PackageRepository packageRepository) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.packageRepository = packageRepository;
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
    public void updateCustomer(Long id, String name, String phoneNumber, String username) {
        Customer customer = getCustomerById(id);

        // Обновява данните на клиента
        customer.setName(name);
        customer.setPhoneNumber(phoneNumber);

        // Обновява потребителското име (ако има свързан потребител)
        if (customer.getUser() != null && username != null && !username.trim().isEmpty()) {
            User user = customer.getUser();

            // Проверка дали новият username е различен
            if (!user.getUsername().equals(username)) {
                // Проверка дали е зает
                if (userRepository.findByUsername(username).isPresent()) {
                    throw new IllegalArgumentException("Username already exists!");
                }
                user.setUsername(username);
                userRepository.save(user); // Запазва промените в User
            }
        }

        customerRepository.save(customer); // Запазва промените в Customer
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + id));

        List<Package> sentPackages = packageRepository.findAllBySender(customer);
        for (Package pkg : sentPackages) {
            pkg.setSender(null);
        }

        List<Package> receivedPackages = packageRepository.findAllByReceiver(customer);
        for (Package pkg : receivedPackages) {
            pkg.setReceiver(null);
        }

        User userToDelete = customer.getUser();

        customerRepository.delete(customer);

        if (userToDelete != null) {
            userRepository.delete(userToDelete);
        }
    }
}