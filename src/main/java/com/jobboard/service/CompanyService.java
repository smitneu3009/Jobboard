package com.jobboard.service;

import com.jobboard.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompanyService {
    void registerCompany(Company company);
    Company findByEmail(String email);
    Company getProfile(int id);
    void updateProfile(Company company);
    void updateCompany(Company company);
    Page<Company> getAllCompaniesPageable(Pageable pageable);
    void deleteCompany(int id);

}