package com.logistics.company.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne; // Променена анотация
import jakarta.persistence.OneToOne;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
// Премахнати са import java.util.HashSet; и import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @CreationTimestamp // Автоматично попълва timestamp-a при създаване
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // Връзка с Customer (Без промяна)
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Customer customer;

    // Връзка с Employee (Без промяна)
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Employee employee;

    // 🌟 Промяна за ManyToOne връзка с Role 🌟
    // Един потребител има ЕДНА роля, но много потребители могат да имат ЕДНА и съща роля.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    public User() {
    }

    // Актуализиран конструктор
    public User(String username, String passwordHash, String email, String firstName, String lastName, String phoneNumber, Role role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    // ... останалите методи (гетъри и сетъри) ...

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Customer getCustomer() {
        return customer;
    }

    // 🌟 Променен гетър за Role 🌟
    public Role getRole() {
        return role;
    }

    // 🌟 Променен сетър за Role 🌟
    public void setRole(Role role) {
        this.role = role;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}