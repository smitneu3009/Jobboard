package com.jobboard.dao;

import com.jobboard.model.JobApplication;
import com.jobboard.model.JobSeeker;
import com.jobboard.model.ApplicationStatus;
import java.util.List;

public interface JobApplicationDao {
    void save(JobApplication application);
    List<JobApplication> findByJobId(int jobId);
    JobApplication findById(int id);
    void updateStatus(int id, ApplicationStatus status);
    List<JobApplication> findByJobSeekerId(int jobSeekerId);
    boolean existsByJobSeekerIdAndJobId(int jobSeekerId, int jobId);
    List<JobApplication> findByJobSeeker(JobSeeker jobSeeker);
    List<JobApplication> findByJobSeekerAndStatus(JobSeeker jobSeeker, ApplicationStatus status);
    void deleteByJobId(int jobId);
}