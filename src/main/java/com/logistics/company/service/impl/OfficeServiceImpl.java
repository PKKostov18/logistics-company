package com.logistics.company.service.impl;

import com.logistics.company.data.Company;
import com.logistics.company.data.Office;
import com.logistics.company.dto.CreateOfficeRequest;
import com.logistics.company.repository.CompanyRepository;
import com.logistics.company.repository.OfficeRepository;
import com.logistics.company.service.OfficeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OfficeServiceImpl implements OfficeService {

    private final OfficeRepository officeRepository;
    private final CompanyRepository companyRepository;

    @Override
    public List<Office> getAllOffices() {
        return officeRepository.findAll();
    }

    @Override
    public void createOffice(CreateOfficeRequest request) {
        Company company = companyRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Office office = Office.builder()
                .name(request.getName())
                .address(request.getAddress())
                .company(company)
                .build();

        officeRepository.save(office);
    }

    @Override
    public void updateOffice(Long id, CreateOfficeRequest request) {
        Office office = officeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Office not found"));

        office.setName(request.getName());
        office.setAddress(request.getAddress());

        officeRepository.save(office);
    }

    @Override
    public void deleteOffice(Long id) {
        officeRepository.deleteById(id);
    }
}