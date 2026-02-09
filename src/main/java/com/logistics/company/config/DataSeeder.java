package com.logistics.company.config;

import com.logistics.company.data.Company;
import com.logistics.company.data.Customer;
import com.logistics.company.data.Employee;
import com.logistics.company.data.EmployeeType;
import com.logistics.company.data.Office;
import com.logistics.company.data.Role;
import com.logistics.company.data.RoleType;
import com.logistics.company.data.User;
import com.logistics.company.repository.CompanyRepository;
import com.logistics.company.repository.CustomerRepository;
import com.logistics.company.repository.EmployeeRepository;
import com.logistics.company.repository.OfficeRepository;
import com.logistics.company.repository.RoleRepository;
import com.logistics.company.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional; // ВАЖНО: Добавен импорт

import java.time.LocalDate;
import java.util.Optional;

@Component
@Transactional // ВАЖНО: Тази анотация държи сесията отворена и решава грешката "detached entity"
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final OfficeRepository officeRepository;
    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    // запазва референции, за да ги ползва между методите
    private Company mainCompany;
    private Office mainOffice;

    public DataSeeder(RoleRepository roleRepository,
                      UserRepository userRepository,
                      CompanyRepository companyRepository,
                      OfficeRepository officeRepository,
                      EmployeeRepository employeeRepository,
                      CustomerRepository customerRepository,
                      PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.officeRepository = officeRepository;
        this.employeeRepository = employeeRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        logger.info("Starting data seeding...");

        seedRoles();
        seedMainCompany();
        seedOffices();
        seedAdminUser();
        seedOfficeEmployee();
        seedCourier();
        seedCustomer();
        seedCourierTwo();

        logger.info("Data seeding completed successfully.");
    }

    private void seedRoles() {
        for (RoleType roleType : RoleType.values()) {
            if (roleRepository.findByName(roleType).isEmpty()) {
                roleRepository.save(Role.builder().name(roleType).build());
                logger.info("Created Role: {}", roleType);
            }
        }
    }

    private void seedMainCompany() {
        String companyName = "LogiTrace International";
        Optional<Company> existingCompany = companyRepository.findByName(companyName);

        if (companyRepository.count() == 0) {
            mainCompany = Company.builder()
                    .name(companyName)
                    .vatNumber("BG999999999")
                    .build();
            companyRepository.save(mainCompany);
            logger.info("Created Main Company: {}", companyName);
        } else {
            mainCompany = companyRepository.findAll().get(0); // взима първата налична
            logger.info("Using existing Company: {}", mainCompany.getName());
        }
    }

    private void seedOffices() {
        String officeName = "Sofia Central";
        Optional<Office> existingOffice = officeRepository.findByName(officeName);

        if (existingOffice.isEmpty()) {
            mainOffice = Office.builder()
                    .name(officeName)
                    .address("Sofia, bul. Vitosha 100")
                    .company(mainCompany)
                    .build();
            officeRepository.save(mainOffice);
            logger.info("Created Office: {}", officeName);
        } else {
            mainOffice = existingOffice.get();
            logger.info("Using existing Office: {}", mainOffice.getName());
        }
    }

    private void seedAdminUser() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            Role role = roleRepository.findByName(RoleType.ADMIN).orElseThrow();
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .email("admin@logitrace.com")
                    .firstName("System")
                    .lastName("Admin")
                    .role(role)
                    .build();
            userRepository.save(admin);
            logger.info("Created Admin User.");
        }
    }

    private void seedOfficeEmployee() {
        String username = "office_empl1";
        if (userRepository.findByUsername(username).isEmpty()) {
            Role role = roleRepository.findByName(RoleType.OFFICE_EMPLOYEE).orElseThrow();

            // създава User
            User user = User.builder()
                    .username(username)
                        .password(passwordEncoder.encode("password_office"))
                    .email("office1@logitrace.com")
                    .firstName("Maria")
                    .lastName("Ivanova")
                    .role(role)
                    .build();
            userRepository.save(user);

            // създава Employee, свързан с User и Office
            Employee employee = Employee.builder()
                    .user(user) // връзката е @MapsId, така че employee взима ID-то на user
                    .office(mainOffice)
                    .employeeType(EmployeeType.OFFICE)
                    .hireDate(LocalDate.now())
                    .build();

            employeeRepository.save(employee);
            logger.info("Created Office Employee: {}", username);
        }
    }

    private void seedCourier() {
        String username = "courier1";
        if (userRepository.findByUsername(username).isEmpty()) {
            Role role = roleRepository.findByName(RoleType.COURIER).orElseThrow();

            User user = User.builder()
                    .username(username)
                    .password(passwordEncoder.encode("password_courier"))
                    .email("courier1@logitrace.com")
                    .firstName("Ivan")
                    .lastName("Petrov")
                    .role(role)
                    .build();
            userRepository.save(user);

            Employee courier = Employee.builder()
                    .user(user)
                    .office(mainOffice)
                    .employeeType(EmployeeType.COURIER)
                    .hireDate(LocalDate.now())
                    .build();

            employeeRepository.save(courier);
            logger.info("Created Courier: {}", username);
        }
    }

    private void seedCourierTwo() {
        String username = "courier2";
        if (userRepository.findByUsername(username).isEmpty()) {
            Role role = roleRepository.findByName(RoleType.COURIER).orElseThrow();

            User user = User.builder()
                    .username(username)
                    .password(passwordEncoder.encode("password_courier2"))
                    .email("courier2@logitrace.com")
                    .firstName("Plamen")
                    .lastName("Kostov")
                    .role(role)
                    .build();
            userRepository.save(user);

            Employee courier = Employee.builder()
                    .user(user)
                    .office(mainOffice)
                    .employeeType(EmployeeType.COURIER)
                    .hireDate(LocalDate.now())
                    .build();

            employeeRepository.save(courier);
            logger.info("Created Courier: {}", username);
        }
    }

    private void seedCustomer() {
        String username = "client1";
        if (userRepository.findByUsername(username).isPresent()) {
            return;
        }

        Role role = roleRepository.findByName(RoleType.CUSTOMER)
                .orElseThrow(() -> new RuntimeException("Error: Role CUSTOMER is not found."));

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode("password_client"))
                .email("client1@gmail.com")
                .firstName("Georgi")
                .lastName("Georgiev")
                .role(role)
                .build();
        userRepository.save(user);


        Customer customer = Customer.builder()
                .user(user)
                .name(user.getFirstName() + " " + user.getLastName())
                .phoneNumber("0888123456")
                .build();

        customerRepository.save(customer);
        logger.info("Created Customer: {}", username);
    }
}