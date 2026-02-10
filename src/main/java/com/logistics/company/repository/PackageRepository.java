package com.logistics.company.repository;

import com.logistics.company.data.Employee;
import com.logistics.company.data.Package;
import com.logistics.company.data.PackageStatus;
import com.logistics.company.data.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {

    // ОПТИМИЗАЦИЯ (N+1 FIX) - зарежда всички свързани данни наведнъж, когато се извиква findAll
    // Всички пратки (LEFT JOIN навсякъде, за да не крие Guest пратки)
    @Override
    @Query("SELECT p FROM Package p " +
            "LEFT JOIN FETCH p.sender s LEFT JOIN FETCH s.user " +
            "LEFT JOIN FETCH p.receiver r LEFT JOIN FETCH r.user " +
            "LEFT JOIN FETCH p.destinationOffice " +
            "LEFT JOIN FETCH p.registeredBy rb LEFT JOIN FETCH rb.user " +
            "LEFT JOIN FETCH p.assignedCourier ac LEFT JOIN FETCH ac.user")
    List<Package> findAll();

    // Оптимизирана версия за клиент (подател ИЛИ получател) - използва се в "My Packages"
    @Query("SELECT p FROM Package p " +
            "JOIN FETCH p.sender s LEFT JOIN FETCH s.user " +
            "JOIN FETCH p.receiver r LEFT JOIN FETCH r.user " +
            "LEFT JOIN FETCH p.destinationOffice " +
            "WHERE s.user.username = :username OR r.user.username = :username")
    List<Package> findAllByUserInvolvement(@Param("username") String username);

    // --- МЕТОДИ ЗА СПРАВКИ (REPORTS) ЗА АДМИН ПАНЕЛА ---

    // Справка F: Пратки, изпратени от клиент
    // EntityGraph за оптимизация
    @EntityGraph(attributePaths = {"sender.user", "receiver.user", "destinationOffice", "registeredBy.user"})
    List<Package> findAllBySender_Id(Long senderCustomerId);

    // Справка G: Пратки, получени от клиент
    @EntityGraph(attributePaths = {"sender.user", "receiver.user", "destinationOffice", "registeredBy.user"})
    List<Package> findAllByReceiver_Id(Long receiverCustomerId);

    // Справка D: Пратки, регистрирани от служител
    @EntityGraph(attributePaths = {"sender.user", "receiver.user", "destinationOffice", "registeredBy.user"})
    List<Package> findAllByRegisteredBy_User_Id(Long employeeUserId);

    // Справка H: Приходи за период
    @Query("SELECT SUM(p.price) FROM Package p WHERE p.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal calculateIncomeForPeriod(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);


    // --- МЕТОДИ ЗА СТАТУСИ И КУРИЕРИ ---
    // Pending пратки (LEFT JOIN) - Справка E
    @Query("SELECT p FROM Package p " +
            "LEFT JOIN FETCH p.sender s LEFT JOIN FETCH s.user " +
            "LEFT JOIN FETCH p.receiver r LEFT JOIN FETCH r.user " +
            "LEFT JOIN FETCH p.destinationOffice " +
            "WHERE p.status <> :status")
    List<Package> findAllByStatusNot(@Param("status") PackageStatus status);

    // За Куриерския контролер - само пратките на конкретния куриер, които не са доставени
    List<Package> findAllByAssignedCourierAndStatusNot(Employee assignedCourier, PackageStatus status);

    // За "Свободни пратки" (Available) - нямат куриер и са регистрирани
    List<Package> findAllByAssignedCourierIsNullAndStatus(PackageStatus status);

    // Търсене в свободни пратки
    List<Package> findAllByAssignedCourierIsNullAndStatusAndDeliveryAddressContainingIgnoreCase(PackageStatus status, String address);

    // Търсене по номер
    Optional<Package> findByTrackingNumberAndAssignedCourier_User(String trackingNumber, User user);
    Optional<Package> findByTrackingNumber(String trackingNumber);
}