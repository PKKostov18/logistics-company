package com.logistics.company.dto;

import lombok.Getter;

/**
 * DTO за представяне на Client данни в отговорите на API-то
 * Предотвратява проблеми с безкрайна рекурсия при сериализацията в JSON и скрива чувствителни данни на потребителя
 */

@Getter
public class ClientResponse {
    private Long id;
    private String name;
    private String phoneNumber;
    private String username; // само username, без целия User обект

    public ClientResponse(Long id, String name, String phoneNumber, String username) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.username = username;
    }
}