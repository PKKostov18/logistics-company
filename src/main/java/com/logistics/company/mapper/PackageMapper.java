package com.logistics.company.mapper;

import com.logistics.company.data.Customer;
import com.logistics.company.data.Package;
import com.logistics.company.data.PackageStatus;
import com.logistics.company.data.User;
import com.logistics.company.dto.CreatePackageRequest;
import com.logistics.company.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class PackageMapper {

    // Сменяме CustomerRepository с UserRepository, защото търсим по телефон (който е в User)
    private final UserRepository userRepository;

    public PackageMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Package toEntity(CreatePackageRequest request) {
        // 1. Намираме подателя по телефон
        User senderUser = userRepository.findByPhoneNumber(request.getSenderPhoneNumber())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Sender with phone " + request.getSenderPhoneNumber() + " not found"));

        Customer sender = senderUser.getCustomer();
        if (sender == null) {
            throw new IllegalArgumentException("User with phone " + request.getSenderPhoneNumber() + " is not a customer");
        }

        // 2. Намираме получателя по телефон
        User receiverUser = userRepository.findByPhoneNumber(request.getReceiverPhoneNumber())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Receiver with phone " + request.getReceiverPhoneNumber() + " not found"));

        Customer receiver = receiverUser.getCustomer();
        if (receiver == null) {
            throw new IllegalArgumentException("User with phone " + request.getReceiverPhoneNumber() + " is not a customer");
        }

        // 3. Създаваме обекта
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