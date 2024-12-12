package com.jobboard.dao;

import com.jobboard.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompanyDao {
    void registerCompany(Company company);
    Company findByEmail(String email);
    Company getProfile(int id);
    void updateProfile(Company company);
    void updateCompany(Company company);
    void save(Company company);
    Page<Company> findAllPaginated(Pageable pageable);
    void deleteById(int id);

}