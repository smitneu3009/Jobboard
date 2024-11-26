package com.jobboard.service;

import com.jobboard.model.ApplicationStatus;
import com.jobboard.model.JobApplication;
import java.util.List;

public interface JobApplicationService {
    void save(JobApplication application);
    List<JobApplication> findByJobId(int jobId);
    JobApplication findById(int id);
    void updateStatus(int id, ApplicationStatus status);
    List<JobApplication> findByJobSeekerId(int jobSeekerId);

}