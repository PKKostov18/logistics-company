package com.logistics.company.mapper;

import com.logistics.company.data.Customer;
import com.logistics.company.data.Package;
import com.logistics.company.data.PackageStatus;
import com.logistics.company.dto.CreatePackageRequest;
import com.logistics.company.repository.CustomerRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper component for converting Package DTOs to entities.
 */
@Component
public class PackageMapper {

    private final CustomerRepository customerRepository;

    public PackageMapper(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Converts a CreatePackageRequest DTO to a Package entity.
     * Fetches sender and receiver Customer entities by ID.
     *
     * @param request the DTO containing package creation data
     * @return a Package entity ready to be persisted
     * @throws IllegalArgumentException if sender or receiver is not found
     */
    public Package toEntity(CreatePackageRequest request) {
        Customer sender = customerRepository.findById(request.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Sender with ID " + request.getSenderId() + " not found"));

        Customer receiver = customerRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Receiver with ID " + request.getReceiverId() + " not found"));

        return Package.builder()
                .sender(sender)
                .receiver(receiver)
                .deliveryType(request.getDeliveryType())
                .deliveryAddress(request.getDeliveryAddress())
                .weightKg(request.getWeight())
                .status(PackageStatus.REGISTERED)
                .build();
    }
}
