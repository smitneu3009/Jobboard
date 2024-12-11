package com.jobboard.serviceImpl;

import com.jobboard.dao.CompanyDao;
import com.jobboard.model.Company;
import com.jobboard.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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
    @Override
    public void updateCompany(Company company) {
        companyDao.update(company);
    }
    
    @Override
    public Page<Company> getAllCompaniesPageable(Pageable pageable) {
        return companyDao.findAllPaginated(pageable);
    }
    @Override
    public void deleteCompany(int id) {
        companyDao.deleteById(id);
    }

}