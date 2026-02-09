package com.logistics.company.repository;

import com.logistics.company.data.Employee;
import com.logistics.company.data.Package;
import com.logistics.company.data.PackageStatus;
import com.logistics.company.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection; // Добавен импорт
import java.util.List;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {

    List<Package> findAllByRegisteredBy_User_Id(Long employeeId);

    List<Package> findAllBySender_User_Id(Long senderId);
    List<Package> findAllByReceiver_User_Id(Long receiverId);

    List<Package> findAllByStatusNot(PackageStatus status);

    List<Package> findAllByStatus(PackageStatus status);

    List<Package> findAllByStatusIn(Collection<PackageStatus> statuses);
    List<Package> findAllByAssignedCourierAndStatusNot(Employee assignedCourier, PackageStatus status);

    List<Package> findAllByAssignedCourierIsNullAndStatus(PackageStatus status);
    List<Package> findAllByAssignedCourierIsNullAndStatusAndDeliveryAddressContainingIgnoreCase(PackageStatus status, String address);
}