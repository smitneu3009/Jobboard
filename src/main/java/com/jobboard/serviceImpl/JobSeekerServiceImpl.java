package com.jobboard.serviceImpl;

import com.jobboard.dao.JobSeekerDao;
import com.jobboard.model.JobSeeker;
import com.jobboard.service.JobSeekerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JobSeekerServiceImpl implements JobSeekerService {

    @Autowired
    private JobSeekerDao jobSeekerDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void applyForJob(JobSeeker jobSeeker) {
        jobSeekerDao.applyForJob(jobSeeker);
    }

    @Override
    public JobSeeker getProfile(int id) {
        return jobSeekerDao.getProfile(id);
    }

    @Override
    public void updateProfile(JobSeeker jobSeeker) {
        jobSeekerDao.updateProfile(jobSeeker);
    }

    @Override
    public void registerJobSeeker(JobSeeker jobSeeker) {
        // Encrypt password before saving
        jobSeeker.setPassword(passwordEncoder.encode(jobSeeker.getPassword()));
        jobSeekerDao.registerJobSeeker(jobSeeker);
    }
}