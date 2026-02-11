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
        request.setPhoneNumber("0882008835");
        request.setEmail("ivan@gmail.com");

        when(userRepository.findByPhoneNumber("0882008835")).thenReturn(Optional.of(new User()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerNewUser(request));

        assertEquals("Phone number is already registered.", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerNewUser_ShouldThrow_WhenEmailExists() {
        RegistrationRequest request = new RegistrationRequest();
        request.setPhoneNumber("0882008835");
        request.setEmail("ivan@gmail.com");

        when(userRepository.findByPhoneNumber("0882008835")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("ivan@gmail.com")).thenReturn(Optional.of(new User()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerNewUser(request));

        assertEquals("Email address is already registered.", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerNewUser_ShouldThrow_WhenRoleNotFound() {
        RegistrationRequest request = new RegistrationRequest();
        request.setPhoneNumber("0882008835");
        request.setEmail("ivan@gmail.com");

        when(userRepository.findByPhoneNumber("0882008835")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("ivan@gmail.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName(RoleType.CUSTOMER)).thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> userService.registerNewUser(request));

        assertEquals("Role 'CUSTOMER' not found in the database.", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerNewUser_ShouldSaveNewUser_WhenValidRequestAndNoExistingCustomer() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("ivan");
        request.setPassword("parola123");
        request.setEmail("ivan@gmail.com");
        request.setFirstName("Ivan");
        request.setLastName("Dimov");
        request.setPhoneNumber("0882008835");

        Role role = new Role();
        role.setName(RoleType.CUSTOMER);

        when(userRepository.findByPhoneNumber("0882008835")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("ivan@gmail.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName(RoleType.CUSTOMER)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("parola123")).thenReturn("encodedPassword");
        when(customerRepository.findByPhoneNumber("0882008835")).thenReturn(Optional.empty());

        userService.registerNewUser(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("ivan", savedUser.getUsername());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals("Ivan Dimov", savedUser.getCustomer().getName());
        assertEquals("0882008835", savedUser.getCustomer().getPhoneNumber());
    }

    @Test
    void registerNewUser_ShouldUpdateExistingCustomer_WhenCustomerExists() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("ivan");
        request.setPassword("parola123");
        request.setEmail("ivan@gmail.com");
        request.setFirstName("Ivan");
        request.setLastName("Dimov");
        request.setPhoneNumber("0882008835");

        Role role = new Role();
        role.setName(RoleType.CUSTOMER);

        Customer existingCustomer = new Customer();
        existingCustomer.setPhoneNumber("0882008835");

        when(userRepository.findByPhoneNumber("0882008835")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("ivan@gmail.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName(RoleType.CUSTOMER)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("parola123")).thenReturn("encodedPassword");
        when(customerRepository.findByPhoneNumber("0882008835")).thenReturn(Optional.of(existingCustomer));

        userService.registerNewUser(request);

        assertEquals("Ivan Dimov", existingCustomer.getName());
        assertNotNull(existingCustomer.getUser());
        verify(userRepository).save(existingCustomer.getUser());
    }

    @Test
    void findByUsername_ShouldReturnUser_WhenFound() {
        User user = new User();
        user.setUsername("ivan");

        when(userRepository.findByUsername("ivan")).thenReturn(Optional.of(user));

        User found = userService.findByUsername("ivan");
        assertEquals("ivan", found.getUsername());
    }

    @Test
    void findByUsername_ShouldThrow_WhenNotFound() {
        when(userRepository.findByUsername("ivan")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.findByUsername("ivan"));

        assertEquals("User not found with username: ivan", exception.getMessage());
    }
}
