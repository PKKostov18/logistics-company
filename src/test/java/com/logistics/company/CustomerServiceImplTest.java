package com.logistics.company;

import com.logistics.company.data.Customer;
import com.logistics.company.data.User;
import com.logistics.company.repository.CustomerRepository;
import com.logistics.company.repository.PackageRepository;
import com.logistics.company.repository.UserRepository;
import com.logistics.company.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PackageRepository packageRepository;

    private CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerServiceImpl(customerRepository, userRepository, packageRepository);
    }

    @Test
    void getAllCustomers_ShouldReturnList_WhenCustomersExist() {

        Customer c1 = Customer.builder().id(1L).name("Ivan").build();
        Customer c2 = Customer.builder().id(2L).name("Maria").build();

        when(customerRepository.findAll()).thenReturn(List.of(c1, c2));

        List<Customer> result = customerService.getAllCustomers();

        assertEquals(2, result.size());
        assertEquals("Ivan", result.get(0).getName());
        assertEquals("Maria", result.get(1).getName());
    }

    @Test
    void getAllCustomers_ShouldReturnEmptyList_WhenNoCustomers() {

        when(customerRepository.findAll()).thenReturn(List.of());

        List<Customer> result = customerService.getAllCustomers();

        assertTrue(result.isEmpty());
    }

    @Test
    void getCustomerById_ShouldReturnCustomer_WhenExists() {

        Customer customer = Customer.builder()
                .id(1L)
                .name("Ivan")
                .build();

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        Customer result = customerService.getCustomerById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Ivan", result.getName());
    }

    @Test
    void getCustomerById_ShouldThrowException_WhenNotExists() {

        when(customerRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> customerService.getCustomerById(1L)
        );
    }

    @Test
    void updateCustomer_ShouldUpdateCustomerOnly_WhenUsernameNull() {

        Customer customer = Customer.builder()
                .id(1L)
                .name("Old Name")
                .phoneNumber("111")
                .build();

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        customerService.updateCustomer(1L, "New Name", "222", null);

        assertEquals("New Name", customer.getName());
        assertEquals("222", customer.getPhoneNumber());

        verify(customerRepository).save(customer);
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateCustomer_ShouldUpdateCustomerAndUsername_WhenValidUsername() {

        User user = User.builder()
                .username("oldUsername")
                .build();

        Customer customer = Customer.builder()
                .id(1L)
                .name("Ivan")
                .phoneNumber("111")
                .user(user)
                .build();

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(userRepository.findByUsername("newUsername"))
                .thenReturn(Optional.empty());

        customerService.updateCustomer(1L, "Ivan Updated", "222", "newUsername");

        assertEquals("Ivan Updated", customer.getName());
        assertEquals("222", customer.getPhoneNumber());
        assertEquals("newUsername", user.getUsername());

        verify(userRepository).save(user);
        verify(customerRepository).save(customer);
    }

    @Test
    void updateCustomer_ShouldThrowException_WhenUsernameExists() {

        User user = User.builder()
                .username("oldUsername")
                .build();

        Customer customer = Customer.builder()
                .id(1L)
                .user(user)
                .build();

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(userRepository.findByUsername("existingUsername"))
                .thenReturn(Optional.of(new User()));

        assertThrows(
                IllegalArgumentException.class,
                () -> customerService.updateCustomer(1L, "Name", "123", "existingUsername")
        );
    }

    @Test
    void updateCustomer_ShouldNotUpdateUsername_WhenSameUsername() {

        User user = User.builder()
                .username("sameUsername")
                .build();

        Customer customer = Customer.builder()
                .id(1L)
                .user(user)
                .build();

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        customerService.updateCustomer(1L, "Name", "123", "sameUsername");

        verify(userRepository, never()).save(any());
        verify(customerRepository).save(customer);
    }

    @Test
    void deleteCustomer_ShouldCallRepository() {

        customerService.deleteCustomer(1L);

        verify(customerRepository).deleteById(1L);
    }
}
