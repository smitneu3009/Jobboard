package com.jobboard.serviceImpl;

import com.jobboard.dao.CompanyDao;
import com.jobboard.model.Company;
import com.jobboard.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    private CompanyDao companyDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void registerCompany(Company company) {
        company.setPassword(passwordEncoder.encode(company.getPassword()));
        companyDao.registerCompany(company);
    }

    @Override
    public Company findByEmail(String email) {
        return companyDao.findByEmail(email);
    }

    @Override
    public Company getProfile(int id) {
        return companyDao.getProfile(id);
    }

    @Override
    public void updateProfile(Company company) {
        if (company.getPassword() != null && !company.getPassword().trim().isEmpty()) {
            company.setPassword(passwordEncoder.encode(company.getPassword()));
        }
        companyDao.updateProfile(company);
    }
    
    
}