package com.jobboard.dao;

import java.util.List;

import com.jobboard.model.JobApplication;
import com.jobboard.model.JobSeeker;

public interface JobApplicationDao {
    void save(JobApplication application);
    List<JobApplication> findByJobSeeker(JobSeeker jobSeeker);
    JobApplication findById(int id);
    boolean existsByJobSeekerIdAndJobId(int jobSeekerId, int jobId);

}