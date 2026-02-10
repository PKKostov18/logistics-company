package com.logistics.company.service;

import com.logistics.company.data.*;
import com.logistics.company.dto.RegistrationRequest;
import com.logistics.company.repository.CustomerRepository;
import com.logistics.company.repository.RoleRepository;
import com.logistics.company.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void registerNewUser_Success_NewCustomerCreated() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("ivan_petrov");
        request.setPassword("parola123");
        request.setEmail("ivan.petrov@example.com");
        request.setPhoneNumber("+359888123456");
        request.setFirstName("Иван");
        request.setLastName("Петров");

        Role customerRole = new Role();
        customerRole.setName(RoleType.CUSTOMER);

        when(userRepository.findByPhoneNumber("+359888123456")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("ivan.petrov@example.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName(RoleType.CUSTOMER)).thenReturn(Optional.of(customerRole));
        when(passwordEncoder.encode("parola123")).thenReturn("encodedPassword");
        when(customerRepository.findByPhoneNumber("+359888123456")).thenReturn(Optional.empty());

        userService.registerNewUser(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("ivan_petrov", savedUser.getUsername());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals("Иван Петров", savedUser.getCustomer().getName());
        assertEquals("+359888123456", savedUser.getCustomer().getPhoneNumber());
    }

    @Test
    void registerNewUser_Success_ExistingCustomerUpdated() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("maria_ivanova");
        request.setPassword("parola123");
        request.setEmail("maria.ivanova@example.com");
        request.setPhoneNumber("+359887654321");
        request.setFirstName("Мария");
        request.setLastName("Иванова");

        Role customerRole = new Role();
        customerRole.setName(RoleType.CUSTOMER);

        Customer existingCustomer = Customer.builder()
                .name("Старо Име")
                .phoneNumber("+359887654321")
                .build();

        when(userRepository.findByPhoneNumber("+359887654321")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("maria.ivanova@example.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName(RoleType.CUSTOMER)).thenReturn(Optional.of(customerRole));
        when(passwordEncoder.encode("parola123")).thenReturn("encodedPassword");
        when(customerRepository.findByPhoneNumber("+359887654321")).thenReturn(Optional.of(existingCustomer));

        userService.registerNewUser(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("Мария Иванова", savedUser.getCustomer().getName());
        assertEquals(savedUser, savedUser.getCustomer().getUser());
    }

    @Test
    void registerNewUser_Fails_WhenPhoneExists() {
        RegistrationRequest request = new RegistrationRequest();
        request.setPhoneNumber("+359888000111");

        when(userRepository.findByPhoneNumber("+359888000111")).thenReturn(Optional.of(new User()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerNewUser(request));

        assertEquals("Phone number is already registered.", exception.getMessage());
    }

    @Test
    void registerNewUser_Fails_WhenEmailExists() {
        RegistrationRequest request = new RegistrationRequest();
        request.setPhoneNumber("+359888222333");
        request.setEmail("petar.georgiev@example.com");

        when(userRepository.findByPhoneNumber("+359888222333")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("petar.georgiev@example.com")).thenReturn(Optional.of(new User()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerNewUser(request));

        assertEquals("Email address is already registered.", exception.getMessage());
    }

    @Test
    void registerNewUser_Fails_WhenRoleMissing() {
        RegistrationRequest request = new RegistrationRequest();
        request.setPhoneNumber("+359888333444");
        request.setEmail("elena.stoyanova@example.com");

        when(userRepository.findByPhoneNumber("+359888333444")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("elena.stoyanova@example.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName(RoleType.CUSTOMER)).thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> userService.registerNewUser(request));

        assertEquals("Role 'CUSTOMER' not found in the database.", exception.getMessage());
    }


    @Test
    void findByUsername_Success() {
        User user = new User();
        user.setUsername("ivan_petrov");

        when(userRepository.findByUsername("ivan_petrov")).thenReturn(Optional.of(user));

        User foundUser = userService.findByUsername("ivan_petrov");
        assertEquals("ivan_petrov", foundUser.getUsername());
    }

    @Test
    void findByUsername_NotFound() {
        when(userRepository.findByUsername("maria_ivanova")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.findByUsername("maria_ivanova"));

        assertEquals("User not found with username: maria_ivanova", exception.getMessage());
    }

}
