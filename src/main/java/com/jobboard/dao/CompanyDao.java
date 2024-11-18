package com.jobboard.dao;

import com.jobboard.model.Company;

public interface CompanyDao {
    void registerCompany(Company company);
    Company findByEmail(String email);
    Company getProfile(int id);
    void updateProfile(Company company);
}