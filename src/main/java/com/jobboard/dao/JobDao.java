package com.jobboard.dao;

import com.jobboard.model.Job;
import com.jobboard.model.Company;
import java.util.List;

public interface JobDao {
    void save(Job job);
    void update(Job job);
    void delete(int id);
    Job findById(int id);
    List<Job> findByCompany(Company company);
    
    int countActiveJobsByCompany(Company company);
    int countTotalApplicationsByCompany(Company company);
}