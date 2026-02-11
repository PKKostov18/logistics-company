package com.logistics.company.service;

import com.logistics.company.data.Customer;
import java.util.List;

/**
 * Сервизен интерфейс за управление на клиенти в логистичната компания.
 * Предоставя основни операции за извличане, актуализиране и изтриване на клиенти.
 */

public interface CustomerService {
    List<Customer> getAllCustomers();
    Customer getCustomerById(Long id);
    void updateCustomer(Long id, String name, String phoneNumber, String username);
    void deleteCustomer(Long id);
}