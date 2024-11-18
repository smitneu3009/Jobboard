package com.jobboard.service;

import com.jobboard.model.JobSeeker;
import java.util.List;

public interface JobSeekerService {
    void applyForJob(JobSeeker jobSeeker);
    JobSeeker getProfile(int id);
    void updateProfile(JobSeeker jobSeeker);
    void registerJobSeeker(JobSeeker jobSeeker);
    JobSeeker findByEmail(String email);
    List<JobSeeker> getAllJobSeekers();
    void deleteJobSeeker(int id);
}