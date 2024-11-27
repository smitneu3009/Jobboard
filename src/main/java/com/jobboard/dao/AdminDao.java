package com.jobboard.dao;

import com.jobboard.model.Admin;

public interface AdminDao {
    Admin findByAdminEmail(String email);
    void save(Admin admin);  
    void update(Admin admin);  
    void delete(Admin admin);
}