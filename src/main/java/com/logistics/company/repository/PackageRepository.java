package com.logistics.company.repository;

import com.logistics.company.data.Package;
import com.logistics.company.data.PackageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {

    // Всички пратки, регистрирани от даден служител
    List<Package> findAllByRegisteredBy_User_Id(Long employeeId);

    // Клиент вижда изпратени и получени пратки
    List<Package> findAllBySender_User_Id(Long senderId);
    List<Package> findAllByReceiver_User_Id(Long receiverId);

    // Пратки, които са изпратени, но не са получени (т.е. статус != RECEIVED или DELIVERED)
    List<Package> findAllByStatusNot(PackageStatus status);

    List<Package> findAllByStatus(PackageStatus status);
}