package com.jobboard.service;

import com.jobboard.model.Job;
import com.jobboard.model.Company;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface JobService {
    void saveJob(Job job);
    void updateJob(Job job);
    void deleteJob(int id);
    Job findById(int id);
    List<Job> findByCompany(Company company);
    int countActiveJobsByCompany(Company company);
    int countTotalApplicationsByCompany(Company company);
    List<Job> findJobsWithFilters(String category, String location, Double minPay, Double maxPay, String jobType);
    List<String> findAllCategories();
    List<String> findAllLocations();
    List<Job> getAllJobs(); 
    Page<Job> findAllJobs(Pageable pageable);
    List<Job> findByCompanyId(int companyId);

}