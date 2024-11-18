package com.jobboard.dao;

import com.jobboard.model.Admin;

public interface AdminDao {
    Admin findByAdminEmail(String email);
    void save(Admin admin);  // Add this method
    void update(Admin admin);  // Optional but recommended
    void delete(Admin admin);  // Optional but recommended
}