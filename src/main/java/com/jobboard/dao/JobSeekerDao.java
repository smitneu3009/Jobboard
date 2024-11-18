package com.jobboard.dao;

import com.jobboard.model.JobSeeker;
import java.util.List;

public interface JobSeekerDao {
    void applyForJob(JobSeeker jobSeeker);
    JobSeeker getProfile(int id);
    void updateProfile(JobSeeker jobSeeker);
    void registerJobSeeker(JobSeeker jobSeeker);
    JobSeeker findByEmail(String email);
    List<JobSeeker> findAll();
    void deleteById(int id);
}