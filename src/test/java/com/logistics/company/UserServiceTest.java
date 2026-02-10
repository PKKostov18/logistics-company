package com.logistics.company;

import com.logistics.company.data.*;
import com.logistics.company.dto.RegistrationRequest;
import com.logistics.company.repository.CustomerRepository;
import com.logistics.company.repository.RoleRepository;
import com.logistics.company.repository.UserRepository;
import com.logistics.company.service.UserService;
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
    void registerNewUser_ShouldThrow_WhenPhoneNumberExists() {
        RegistrationRequest request = new RegistrationRequest();
        request.setPhoneNumber("123456");
        request.setEmail("test@example.com");

        when(userRepository.findByPhoneNumber("123456")).thenReturn(Optional.of(new User()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerNewUser(request));

        assertEquals("Phone number is already registered.", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerNewUser_ShouldThrow_WhenEmailExists() {
        RegistrationRequest request = new RegistrationRequest();
        request.setPhoneNumber("123456");
        request.setEmail("test@example.com");

        when(userRepository.findByPhoneNumber("123456")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerNewUser(request));

        assertEquals("Email address is already registered.", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerNewUser_ShouldThrow_WhenRoleNotFound() {
        RegistrationRequest request = new RegistrationRequest();
        request.setPhoneNumber("123456");
        request.setEmail("test@example.com");

        when(userRepository.findByPhoneNumber("123456")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName(RoleType.CUSTOMER)).thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> userService.registerNewUser(request));

        assertEquals("Role 'CUSTOMER' not found in the database.", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerNewUser_ShouldSaveNewUser_WhenValidRequestAndNoExistingCustomer() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("john");
        request.setPassword("password");
        request.setEmail("john@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPhoneNumber("123456");

        Role role = new Role();
        role.setName(RoleType.CUSTOMER);

        when(userRepository.findByPhoneNumber("123456")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName(RoleType.CUSTOMER)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(customerRepository.findByPhoneNumber("123456")).thenReturn(Optional.empty());

        userService.registerNewUser(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("john", savedUser.getUsername());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals("John Doe", savedUser.getCustomer().getName());
        assertEquals("123456", savedUser.getCustomer().getPhoneNumber());
    }

    @Test
    void registerNewUser_ShouldUpdateExistingCustomer_WhenCustomerExists() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("john");
        request.setPassword("password");
        request.setEmail("john@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPhoneNumber("123456");

        Role role = new Role();
        role.setName(RoleType.CUSTOMER);

        Customer existingCustomer = new Customer();
        existingCustomer.setPhoneNumber("123456");

        when(userRepository.findByPhoneNumber("123456")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName(RoleType.CUSTOMER)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(customerRepository.findByPhoneNumber("123456")).thenReturn(Optional.of(existingCustomer));

        userService.registerNewUser(request);

        assertEquals("John Doe", existingCustomer.getName());
        assertNotNull(existingCustomer.getUser());
        verify(userRepository).save(existingCustomer.getUser());
    }

    @Test
    void findByUsername_ShouldReturnUser_WhenFound() {
        User user = new User();
        user.setUsername("john");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        User found = userService.findByUsername("john");
        assertEquals("john", found.getUsername());
    }

    @Test
    void findByUsername_ShouldThrow_WhenNotFound() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.findByUsername("john"));

        assertEquals("User not found with username: john", exception.getMessage());
    }
}
