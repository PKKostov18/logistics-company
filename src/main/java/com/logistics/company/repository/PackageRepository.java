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
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {

    // ОПТИМИЗАЦИЯ (N+1 FIX) - зарежда всички свързани данни наведнъж, когато се извиква findAll
    @Override
    @EntityGraph(attributePaths = {"sender.user", "receiver.user", "destinationOffice", "registeredBy.user", "assignedCourier.user"})
    List<Package> findAll();

    // Оптимизирана версия за търсене по служител (registeredBy)
    @Query("SELECT p FROM Package p " +
            "JOIN FETCH p.sender s JOIN FETCH s.user " +
            "JOIN FETCH p.receiver r JOIN FETCH r.user " +
            "LEFT JOIN FETCH p.destinationOffice " +
            "WHERE p.registeredBy.user.id = :employeeId")
    List<Package> findAllByRegisteredBy_User_Id(@Param("employeeId") Long employeeId);

    // Оптимизирана версия за клиент (подател ИЛИ получател)
    @Query("SELECT p FROM Package p " +
            "JOIN FETCH p.sender s JOIN FETCH s.user " +
            "JOIN FETCH p.receiver r JOIN FETCH r.user " +
            "LEFT JOIN FETCH p.destinationOffice " +
            "WHERE s.user.username = :username OR r.user.username = :username")
    List<Package> findAllByUserInvolvement(@Param("username") String username);

    List<Package> findAllBySender_User_Id(Long senderId);
    List<Package> findAllByReceiver_User_Id(Long receiverId);

    // Оптимизирана версия за статус (Pending shipments)
    @Query("SELECT p FROM Package p " +
            "JOIN FETCH p.sender s JOIN FETCH s.user " +
            "JOIN FETCH p.receiver r JOIN FETCH r.user " +
            "WHERE p.status <> :status")
    List<Package> findAllByStatusNot(@Param("status") PackageStatus status);

    List<Package> findAllByStatus(PackageStatus status);
    List<Package> findAllByStatusIn(Collection<PackageStatus> statuses);

    // За Куриерския контролер
    List<Package> findAllByAssignedCourierAndStatusNot(Employee assignedCourier, PackageStatus status);

    List<Package> findAllByAssignedCourierIsNullAndStatus(PackageStatus status);
    List<Package> findAllByAssignedCourierIsNullAndStatusAndDeliveryAddressContainingIgnoreCase(PackageStatus status, String address);

    Optional<Package> findByTrackingNumberAndAssignedCourier_User(String trackingNumber, User user);
    Optional<Package> findByTrackingNumber(String trackingNumber);

    // СПРАВКА ЗА ПРИХОДИ
    @Query("SELECT SUM(p.price) FROM Package p WHERE p.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal calculateIncomeForPeriod(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
}