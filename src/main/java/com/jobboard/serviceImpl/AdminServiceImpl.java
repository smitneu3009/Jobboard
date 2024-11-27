package com.jobboard.serviceImpl;

import com.jobboard.dao.AdminDao;
import com.jobboard.model.Admin;
import com.jobboard.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminDao adminDao;

    @Override
    public Admin findByAdminEmail(String email) {
        return adminDao.findByAdminEmail(email);
    }

    public void saveAdmin(Admin admin) {
        // No password encoding
        adminDao.save(admin);
    }
}