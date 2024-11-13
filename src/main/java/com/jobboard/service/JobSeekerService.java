package com.jobboard.service;

import com.jobboard.model.JobSeeker;

public interface JobSeekerService {
    void applyForJob(JobSeeker jobSeeker);
    JobSeeker getProfile(int id);
    void updateProfile(JobSeeker jobSeeker);
    void registerJobSeeker(JobSeeker jobSeeker);
}
