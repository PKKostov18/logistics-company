package com.logistics.company.service.impl;

import com.logistics.company.data.Company;
import com.logistics.company.data.Office;
import com.logistics.company.dto.CreateOfficeRequest;
import com.logistics.company.repository.CompanyRepository;
import com.logistics.company.repository.OfficeRepository;
import com.logistics.company.service.OfficeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfficeServiceImpl implements OfficeService {

    private final OfficeRepository officeRepository;
    private final CompanyRepository companyRepository;

    public OfficeServiceImpl(OfficeRepository officeRepository, CompanyRepository companyRepository) {
        this.officeRepository = officeRepository;
        this.companyRepository = companyRepository;
    }

    @Override
    public List<Office> getAllOffices() {
        return officeRepository.findAll();
    }

    @Override
    public void createOffice(CreateOfficeRequest request) {
        // fetch-ва се единичната "Main Company" (първата компания в БД)
        Company company = companyRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("No company found. Please create a company first."));

        Office office = Office.builder()
                .name(request.getName())
                .address(request.getAddress())
                .company(company)
                .build();

        officeRepository.save(office);
    }

    @Override
    public void deleteOffice(Long id) {
        Office office = officeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Office not found with id: " + id));

        officeRepository.delete(office);
    }
}
