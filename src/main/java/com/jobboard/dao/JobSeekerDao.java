package com.jobboard.dao;

import com.jobboard.model.JobSeeker;
import com.jobboard.model.ApplicationStatus;
import com.jobboard.model.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface JobSeekerDao {
    void applyForJob(JobSeeker jobSeeker);
    JobSeeker getProfile(int id);
    void updateProfile(JobSeeker jobSeeker);
    void registerJobSeeker(JobSeeker jobSeeker);
    JobSeeker findByEmail(String email);
    List<JobSeeker> findAll();
    void deleteById(int id);
    
    Page<JobSeeker> findAllPaginated(Pageable pageable);
    
    void saveApplication(JobApplication application);
    List<JobApplication> findApplicationsByJobSeeker(JobSeeker jobSeeker);
    JobApplication findApplicationById(int id);
    boolean existsByJobSeekerIdAndJobId(int jobSeekerId, int jobId);
    List<JobApplication> findByJobSeekerAndStatus(JobSeeker jobSeeker, ApplicationStatus status);
    int countTotalApplications(JobSeeker jobSeeker);
}
