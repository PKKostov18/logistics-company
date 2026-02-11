package com.logistics.company.service.impl;

import com.logistics.company.data.Company;
import com.logistics.company.data.Employee;
import com.logistics.company.data.Office;
import com.logistics.company.data.Package;
import com.logistics.company.dto.CreateOfficeRequest;
import com.logistics.company.repository.CompanyRepository;
import com.logistics.company.repository.OfficeRepository;
import com.logistics.company.repository.PackageRepository;
import com.logistics.company.service.OfficeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OfficeServiceImpl implements OfficeService {

    private final OfficeRepository officeRepository;
    private final CompanyRepository companyRepository;
    private final PackageRepository packageRepository;

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
    @Transactional
    public void deleteOffice(Long id) {
        Office office = officeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Office not found"));

        List<Package> packages = packageRepository.findAllByDestinationOffice(office);
        if (!packages.isEmpty()) {
            throw new IllegalStateException("Cannot delete office! There are active packages assigned to it.");
        }

        if (office.getEmployees() != null) {
            for (Employee employee : office.getEmployees()) {
                employee.setOffice(null);
            }
        }

        officeRepository.deleteById(id);
    }
}