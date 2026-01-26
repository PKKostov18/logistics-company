package com.logistics.company.service;

import com.logistics.company.data.Office;
import com.logistics.company.dto.CreateOfficeRequest;

import java.util.List;

public interface OfficeService {

    List<Office> getAllOffices();

    void createOffice(CreateOfficeRequest request);

    void updateOffice(Long id, CreateOfficeRequest request);

    void deleteOffice(Long id);
}
