package com.logistics.company.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "packages")
public class Package {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "package_id")
    private int id;

    @Column(name = "tracking_number", nullable = false, unique = true, length = 20)
    private String trackingNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_customer_id")
    private Customer sender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_customer_id")
    private Customer receiver;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "registered_by_employee_id")
    private Employee registeredBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_courier_id")
    private Employee assignedCourier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_office_id")
    private Office destinationOffice;

    @Column(name = "weight_kg", nullable = false)
    private float weightKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_type", nullable = false)
    private DeliveryType deliveryType;

    @Column(name = "delivery_address")
    private String deliveryAddress;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PackageStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "received_at")
    private Instant receivedAt;

    public Package() {}

    public Package(int id, String trackingNumber, Customer sender, Customer receiver, Employee registeredBy, Employee assignedCourier, Office destinationOffice, float weightKg, DeliveryType deliveryType, String deliveryAddress, BigDecimal price, PackageStatus status, Instant createdAt, Instant receivedAt) {
        this.id = id;
        this.trackingNumber = trackingNumber;
        this.sender = sender;
        this.receiver = receiver;
        this.registeredBy = registeredBy;
        this.assignedCourier = assignedCourier;
        this.destinationOffice = destinationOffice;
        this.weightKg = weightKg;
        this.deliveryType = deliveryType;
        this.deliveryAddress = deliveryAddress;
        this.price = price;
        this.status = status;
        this.createdAt = createdAt;
        this.receivedAt = receivedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public Customer getSender() {
        return sender;
    }

    public void setSender(Customer sender) {
        this.sender = sender;
    }

    public Customer getReceiver() {
        return receiver;
    }

    public void setReceiver(Customer receiver) {
        this.receiver = receiver;
    }

    public Employee getRegisteredBy() {
        return registeredBy;
    }

    public void setRegisteredBy(Employee registeredBy) {
        this.registeredBy = registeredBy;
    }

    public Employee getAssignedCourier() {
        return assignedCourier;
    }

    public void setAssignedCourier(Employee assignedCourier) {
        this.assignedCourier = assignedCourier;
    }

    public Office getDestinationOffice() {
        return destinationOffice;
    }

    public void setDestinationOffice(Office destinationOffice) {
        this.destinationOffice = destinationOffice;
    }

    public float getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(float weightKg) {
        this.weightKg = weightKg;
    }

    public DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public PackageStatus getStatus() {
        return status;
    }

    public void setStatus(PackageStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(Instant receivedAt) {
        this.receivedAt = receivedAt;
    }
}
