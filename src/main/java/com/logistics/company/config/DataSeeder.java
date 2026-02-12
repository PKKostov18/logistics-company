package com.logistics.company.config;

import com.logistics.company.data.*;
import com.logistics.company.data.Package;
import com.logistics.company.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Зарежда и инициализира начални данни в базата.
 * Създава основни роли, компания, офиси, потребители (администратор, служители, куриери) и клиенти.
 * Също така добавя няколко ръчни пратки за демонстрация.
 */

@Component
@Transactional
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final OfficeRepository officeRepository;
    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;
    private final PackageRepository packageRepository;
    private final PasswordEncoder passwordEncoder;

    private Company mainCompany;
    private Office mainOffice; // Sofia Central

    public DataSeeder(RoleRepository roleRepository,
                      UserRepository userRepository,
                      CompanyRepository companyRepository,
                      OfficeRepository officeRepository,
                      EmployeeRepository employeeRepository,
                      CustomerRepository customerRepository,
                      PackageRepository packageRepository,
                      PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.officeRepository = officeRepository;
        this.employeeRepository = employeeRepository;
        this.customerRepository = customerRepository;
        this.packageRepository = packageRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        logger.info("Starting data seeding...");

        // 1. Основни настройки
        seedRoles();
        seedMainCompany();
        seedOffices();
        seedAdminUser();
        seedOfficeEmployee();
        seedCourier();
        seedCourierTwo();
        seedCustomer();

        // 2. Допълнителни данни
        seedAdditionalOffices();
        seedAdditionalEmployees();
        seedAdditionalCouriers();
        seedAdditionalCustomers();

        // 3. ПРАТКИ (РЪЧНО ДОБАВЯНЕ)
        seedPackagesManual();

        logger.info("Data seeding completed successfully.");
    }

    // --- СЪЩЕСТВУВАЩИ МЕТОДИ ---

    private void seedRoles() {
        for (RoleType roleType : RoleType.values()) {
            if (roleRepository.findByName(roleType).isEmpty()) {
                roleRepository.save(Role.builder().name(roleType).build());
            }
        }
    }

    private void seedMainCompany() {
        String companyName = "LogiTrace International";
        if (companyRepository.count() == 0) {
            mainCompany = Company.builder().name(companyName).vatNumber("BG999999999").build();
            companyRepository.save(mainCompany);
        } else {
            mainCompany = companyRepository.findAll().get(0);
        }
    }

    private void seedOffices() {
        String officeName = "Sofia Central";
        Optional<Office> existingOffice = officeRepository.findByName(officeName);
        if (existingOffice.isEmpty()) {
            // ВЪРНАТО: Без координати, както поиска
            mainOffice = Office.builder()
                    .name(officeName)
                    .address("Sofia, bul. Vitosha 100")
                    .company(mainCompany)
                    .build();
            officeRepository.save(mainOffice);
        } else {
            mainOffice = existingOffice.get();
        }
    }

    private void seedAdminUser() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            Role role = roleRepository.findByName(RoleType.ADMIN).orElseThrow();
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .email("admin@logitrace.com")
                    .firstName("System").lastName("Admin")
                    .role(role).build();
            userRepository.save(admin);
        }
    }

    private void seedOfficeEmployee() {
        createEmployeeIfNotExist("office_empl1", "password_office", "office1@logitrace.com",
                "Shakira", "Popova", "0878013575", RoleType.OFFICE_EMPLOYEE, mainOffice, EmployeeType.OFFICE);
    }

    private void seedCourier() {
        createEmployeeIfNotExist("courier1", "password_courier", "courier1@logitrace.com",
                "Slavi", "Panayotov", "0867963214", RoleType.COURIER, mainOffice, EmployeeType.COURIER);
    }

    private void seedCourierTwo() {
        createEmployeeIfNotExist("courier2", "password_courier2", "courier2@logitrace.com",
                "Nikoleta", "Lozanova", "0888456789", RoleType.COURIER, mainOffice, EmployeeType.COURIER);
    }

    private void seedCustomer() {
        createCustomerIfNotExist("client1", "client1@gmail.com", "Tsar", "Ivan", "0883315067");
    }

    private void seedAdditionalOffices() {
        // ВЪРНАТО: Без координати
        getOrCreateOffice("Burgas Central", "Burgas, str. Aleksandrovska 1");
        getOrCreateOffice("Plovdiv Central", "Plovdiv, str. Ivan Vazov 5");
        getOrCreateOffice("Varna Central", "Varna, blvd. Vladislav Varnenchik 50");
    }

    private void seedAdditionalEmployees() {
        Office sofia = officeRepository.findByName("Sofia Central").orElse(mainOffice);
        Office burgas = officeRepository.findByName("Burgas Central").orElseThrow();
        Office plovdiv = officeRepository.findByName("Plovdiv Central").orElseThrow();
        Office varna = officeRepository.findByName("Varna Central").orElseThrow();

        createEmployeeIfNotExist("sofia_empl2", "pass123", "sofia2@logi.com", "Stanimir", "Etov", "0888101232", RoleType.OFFICE_EMPLOYEE, sofia, EmployeeType.OFFICE);
        createEmployeeIfNotExist("sofia_empl3", "pass123", "sofia3@logi.com", "Hristo", "Stoichkov", "0898353474", RoleType.OFFICE_EMPLOYEE, sofia, EmployeeType.OFFICE);

        createEmployeeIfNotExist("burgas_empl1", "pass123", "burgas1@logi.com", "Dimitar", "Rachkov", "0899123456", RoleType.OFFICE_EMPLOYEE, burgas, EmployeeType.OFFICE);
        createEmployeeIfNotExist("burgas_empl2", "pass123", "burgas2@logi.com", "Plamen", "Kostov", "0899654321", RoleType.OFFICE_EMPLOYEE, burgas, EmployeeType.OFFICE);
        createEmployeeIfNotExist("burgas_empl3", "pass123", "burgas3@logi.com", "Georgi", "Mitrev", "0879187654", RoleType.OFFICE_EMPLOYEE, burgas, EmployeeType.OFFICE);

        createEmployeeIfNotExist("plovdiv_empl1", "pass123", "plovdiv1@logi.com", "Grigor", "Dimitrov", "0878112233", RoleType.OFFICE_EMPLOYEE, plovdiv, EmployeeType.OFFICE);
        createEmployeeIfNotExist("plovdiv_empl2", "pass123", "plovdiv2@logi.com", "Atanas", "Peltekov", "0879425506", RoleType.OFFICE_EMPLOYEE, plovdiv, EmployeeType.OFFICE);

        createEmployeeIfNotExist("varna_empl1", "pass123", "varna1@logi.com", "Philipp", "Bukov", "0899117744", RoleType.OFFICE_EMPLOYEE, varna, EmployeeType.OFFICE);
        createEmployeeIfNotExist("varna_empl2", "pass123", "varna2@logi.com", "Boro", "Purvi", "0886229402", RoleType.OFFICE_EMPLOYEE, varna, EmployeeType.OFFICE);
    }

    private void seedAdditionalCouriers() {
        Office burgas = officeRepository.findByName("Burgas Central").orElse(mainOffice);
        Office plovdiv = officeRepository.findByName("Plovdiv Central").orElse(mainOffice);
        Office varna = officeRepository.findByName("Varna Central").orElse(mainOffice);

        createEmployeeIfNotExist("courier3", "pass_courier", "cour3@logi.com", "Loro", "Piana", "0899673100", RoleType.COURIER, burgas, EmployeeType.COURIER);
        createEmployeeIfNotExist("courier4", "pass_courier", "cour4@logi.com", "Vin", "Diesel", "0868321321", RoleType.COURIER, plovdiv, EmployeeType.COURIER);
        createEmployeeIfNotExist("courier5", "pass_courier", "cour5@logi.com", "Kaloyan", "Andrikov", "0883555670", RoleType.COURIER, varna, EmployeeType.COURIER);
    }

    private void seedAdditionalCustomers() {
        String[][] customersData = {
                {"Petar", "Kavaldzhiev", "0888611922"}, {"Gergana", "Shamanova", "0888333444"},
                {"Dimitar", "Nikolov", "0899123456"}, {"Aleksandar", "Makedonski", "0885123987"},
                {"Maria", "Bakalova", "0899654321"}, {"Stefan", "Diomov", "0877112233"},
                {"Elena", "Mishkova", "0877445566"}, {"Ivan", "Iliev", "0887111111"},
                {"Vanesa", "Marinova", "0887222222"}, {"Kamen", "Hristov", "0898123123"},
                {"Silviya", "Katsarova", "0898321321"}, {"Martin", "Bakalov", "0876555666"},
                {"Daniela", "Bagdasaryan", "0876777888"}, {"Teodora", "Mihaylova", "0885987123"}
        };
        String[] domains = {"@gmail.com", "@icloud.com", "@outlook.com"};

        for (int i = 0; i < customersData.length; i++) {
            int clientNum = i + 2;
            String username = "client" + clientNum;
            String domain = domains[i % domains.length];
            String email = "client" + clientNum + domain;
            createCustomerIfNotExist(username, email, customersData[i][0], customersData[i][1], customersData[i][2]);
        }
    }

    // --- ПРАТКИ ---
    private void seedPackagesManual() {
        if (packageRepository.count() > 0) return;

        // Взимаме офисите
        Office sofia = officeRepository.findByName("Sofia Central").orElse(mainOffice);
        Office burgas = officeRepository.findByName("Burgas Central").orElse(mainOffice);
        Office plovdiv = officeRepository.findByName("Plovdiv Central").orElse(mainOffice);
        Office varna = officeRepository.findByName("Varna Central").orElse(mainOffice);

        // Взимаме произволен служител за "registeredBy"
        List<Employee> allEmployees = employeeRepository.findAll();
        Employee registrar = allEmployees.isEmpty() ? null : allEmployees.get(0);

        // Взимаме ВСИЧКИ клиенти
        List<Customer> allCustomers = customerRepository.findAll();
        if (allCustomers.size() < 10) {
            logger.warn("Not enough customers to seed packages!");
            return;
        }

        // --- 1. СОФИЯ (3 пратки) ---
        // Pkg 1: Sofia -> Sofia Office (Registered)
        Package p1 = new Package();
        p1.setTrackingNumber(generateTrackingNumber());
        p1.setSender(allCustomers.get(0)); // client1
        p1.setReceiver(allCustomers.get(1)); // client2
        p1.setDestinationOffice(sofia);
        p1.setDeliveryType(DeliveryType.TO_OFFICE);
        p1.setWeightKg(0.8);
        p1.setPrice(BigDecimal.valueOf(12.50));
        p1.setDescription("dokumenti za firma");
        p1.setStatus(PackageStatus.REGISTERED);
        p1.setRegisteredBy(registrar);
        p1.setCreatedAt(Instant.now().minusSeconds(172800));
        packageRepository.save(p1);

        // Pkg 2: Sofia -> Address (In Transit)
        Package p2 = new Package();
        p2.setTrackingNumber(generateTrackingNumber());
        p2.setSender(allCustomers.get(0)); // client1
        p2.setReceiver(allCustomers.get(13)); // client14
        p2.setDeliveryAddress("Sofia, Studentski grad bl. 55");
        p2.setDeliveryType(DeliveryType.TO_ADDRESS);
        p2.setWeightKg(6.7);
        p2.setPrice(BigDecimal.valueOf(25.00));
        p2.setDescription("6 kg svinski buzi");
        p2.setStatus(PackageStatus.IN_TRANSIT);
        p2.setRegisteredBy(registrar);
        p2.setCreatedAt(Instant.now().minusSeconds(259200));
        packageRepository.save(p2);

        // Pkg 3: Sofia -> Plovdiv Office (Delivered)
        Package p3 = new Package();
        p3.setTrackingNumber(generateTrackingNumber());
        p3.setSender(allCustomers.get(3));
        p3.setReceiver(allCustomers.get(4));
        p3.setDestinationOffice(plovdiv);
        p3.setDeliveryType(DeliveryType.TO_OFFICE);
        p3.setWeightKg(2.5);
        p3.setPrice(BigDecimal.valueOf(8.50));
        p3.setDescription("chuplivo (porcelan)");
        p3.setStatus(PackageStatus.DELIVERED);
        p3.setRegisteredBy(registrar);
        p3.setCreatedAt(Instant.now().minusSeconds(432000));
        packageRepository.save(p3);

        // --- 2. БУРГАС (2 пратки) ---
        // Pkg 4: Burgas -> Burgas Office (Registered)
        Package p4 = new Package();
        p4.setTrackingNumber(generateTrackingNumber());
        p4.setSender(allCustomers.get(5));
        p4.setReceiver(allCustomers.get(8));
        p4.setDestinationOffice(burgas);
        p4.setDeliveryType(DeliveryType.TO_OFFICE);
        p4.setWeightKg(22.0);
        p4.setPrice(BigDecimal.valueOf(45.00));
        p4.setDescription("avtochasti");
        p4.setStatus(PackageStatus.REGISTERED);
        p4.setRegisteredBy(registrar);
        p4.setCreatedAt(Instant.now().minusSeconds(18000));
        packageRepository.save(p4);

        // Pkg 5: Burgas -> Address (Delivered)
        Package p5 = new Package();
        p5.setTrackingNumber(generateTrackingNumber());
        p5.setSender(allCustomers.get(6));
        p5.setReceiver(allCustomers.get(7));
        p5.setDeliveryAddress("Burgas, Lazur bl. 10");
        p5.setDeliveryType(DeliveryType.TO_ADDRESS);
        p5.setWeightKg(5.5);
        p5.setPrice(BigDecimal.valueOf(5.50));
        p5.setDescription("kashon s lichni veshti");
        p5.setStatus(PackageStatus.DELIVERED);
        p5.setRegisteredBy(registrar);
        p5.setCreatedAt(Instant.now().minusSeconds(345600));
        packageRepository.save(p5);

        // --- 3. ВАРНА (1 пратка) ---
        // Pkg 6: Varna -> Varna Office (Delivered)
        Package p6 = new Package();
        p6.setTrackingNumber(generateTrackingNumber());
        p6.setSender(allCustomers.get(8));
        p6.setReceiver(allCustomers.get(11));
        p6.setDestinationOffice(varna);
        p6.setDeliveryType(DeliveryType.TO_OFFICE);
        p6.setWeightKg(3.8);
        p6.setPrice(BigDecimal.valueOf(18.00));
        p6.setDescription("laptop i periferia");
        p6.setStatus(PackageStatus.DELIVERED);
        p6.setRegisteredBy(registrar);
        p6.setCreatedAt(Instant.now().minusSeconds(518400));
        packageRepository.save(p6);

        // --- 4. ПЛОВДИВ (2 пратки) ---
        // Pkg 7: Plovdiv -> Plovdiv Office (In Transit)
        Package p7 = new Package();
        p7.setTrackingNumber(generateTrackingNumber());
        p7.setSender(allCustomers.get(10));
        p7.setReceiver(allCustomers.get(14));
        p7.setDestinationOffice(plovdiv);
        p7.setDeliveryType(DeliveryType.TO_OFFICE);
        p7.setWeightKg(15.5);
        p7.setPrice(BigDecimal.valueOf(60.00));
        p7.setDescription("office stol");
        p7.setStatus(PackageStatus.IN_TRANSIT);
        p7.setRegisteredBy(registrar);
        p7.setCreatedAt(Instant.now().minusSeconds(86400));
        packageRepository.save(p7);

        // Pkg 8: Plovdiv -> Address (Registered)
        Package p8 = new Package();
        p8.setTrackingNumber(generateTrackingNumber());
        p8.setSender(allCustomers.get(12));
        p8.setReceiver(allCustomers.get(0));
        p8.setDeliveryAddress("Plovdiv, Trakia bl. 120");
        p8.setDeliveryType(DeliveryType.TO_ADDRESS);
        p8.setWeightKg(8.0);
        p8.setPrice(BigDecimal.valueOf(32.00));
        p8.setDescription("kashon s knigi");
        p8.setStatus(PackageStatus.REGISTERED);
        p8.setRegisteredBy(registrar);
        p8.setCreatedAt(Instant.now().minusSeconds(7200));
        packageRepository.save(p8);

        logger.info("Packages seeded manually.");
    }

    private String generateTrackingNumber() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // --- ПОМОЩНИ МЕТОДИ ---

    private void getOrCreateOffice(String name, String address) {
        if (officeRepository.findByName(name).isEmpty()) {
            Office office = Office.builder()
                    .name(name).address(address)
                    .company(mainCompany).build();
            officeRepository.save(office);
        }
    }

    private void createEmployeeIfNotExist(String username, String password, String email,
                                          String firstName, String lastName, String phone,
                                          RoleType roleType, Office office, EmployeeType empType) {
        if (userRepository.findByUsername(username).isEmpty()) {
            Role role = roleRepository.findByName(roleType).orElseThrow();
            User user = User.builder()
                    .username(username).password(passwordEncoder.encode(password))
                    .email(email).firstName(firstName).lastName(lastName).phoneNumber(phone)
                    .role(role).build();
            userRepository.save(user);

            Employee employee = Employee.builder()
                    .user(user).office(office).employeeType(empType)
                    .hireDate(LocalDate.now()).build();
            employeeRepository.save(employee);
        }
    }

    private void createCustomerIfNotExist(String username, String email,
                                          String firstName, String lastName, String phone) {
        if (userRepository.findByUsername(username).isEmpty()) {
            Role role = roleRepository.findByName(RoleType.CUSTOMER).orElseThrow();
            User user = User.builder()
                    .username(username).password(passwordEncoder.encode("password_client"))
                    .email(email).firstName(firstName).lastName(lastName).phoneNumber(phone)
                    .role(role).build();
            userRepository.save(user);

            Customer customer = Customer.builder()
                    .user(user).name(firstName + " " + lastName).phoneNumber(phone).build();
            customerRepository.save(customer);
        }
    }
}