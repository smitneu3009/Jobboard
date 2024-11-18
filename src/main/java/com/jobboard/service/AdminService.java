package com.jobboard.service;

import com.jobboard.model.Admin;

public interface AdminService {
    Admin findByAdminEmail(String email);
    void saveAdmin(Admin admin);
}