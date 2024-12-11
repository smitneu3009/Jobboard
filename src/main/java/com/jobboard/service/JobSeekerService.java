package com.jobboard.service;

import com.jobboard.model.ApplicationStatus;
import com.jobboard.model.JobApplication;
import com.jobboard.model.JobSeeker;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobSeekerService {
    void applyForJob(JobSeeker jobSeeker);
    JobSeeker getProfile(int id);
    void updateProfile(JobSeeker jobSeeker);
    void registerJobSeeker(JobSeeker jobSeeker);
    JobSeeker findByEmail(String email);
    List<JobSeeker> getAllJobSeekers();
    void deleteJobSeeker(int id);
    void saveApplication(JobApplication application); 
    List<JobApplication> getApplicationsByJobSeeker(JobSeeker jobSeeker);
    JobApplication getApplicationById(int id);
    boolean hasAlreadyApplied(int jobSeekerId, int jobId);
    int countTotalApplications(JobSeeker jobSeeker);
    int countApplicationsByStatus(JobSeeker jobSeeker, ApplicationStatus status);
    Page<JobSeeker> getAllJobSeekersPageable(Pageable pageable);

}