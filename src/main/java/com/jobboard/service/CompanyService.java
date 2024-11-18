package com.jobboard.service;

import com.jobboard.model.Company;

public interface CompanyService {
    void registerCompany(Company company);
    Company findByEmail(String email);
    Company getProfile(int id);
    void updateProfile(Company company);
    void updateCompany(Company company);
    
}