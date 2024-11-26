package com.jobboard.serviceImpl;

import com.jobboard.dao.JobApplicationDao;
import com.jobboard.dao.JobSeekerDao;
import com.jobboard.model.ApplicationStatus;
import com.jobboard.model.JobApplication;
import com.jobboard.model.JobSeeker;
import com.jobboard.service.JobSeekerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JobSeekerServiceImpl implements JobSeekerService {

    @Autowired
    private JobSeekerDao jobSeekerDao;
    
    @Autowired
    private JobApplicationDao jobApplicationDao; 

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
        // Only encode password if it's being updated
        if (jobSeeker.getPassword() != null && !jobSeeker.getPassword().trim().isEmpty()) {
            jobSeeker.setPassword(passwordEncoder.encode(jobSeeker.getPassword()));
        }
        jobSeekerDao.updateProfile(jobSeeker);
    }

    @Override
    public void registerJobSeeker(JobSeeker jobSeeker) {
        jobSeeker.setPassword(passwordEncoder.encode(jobSeeker.getPassword()));
        jobSeekerDao.registerJobSeeker(jobSeeker);
    }

    @Override
    public JobSeeker findByEmail(String email) {
        return jobSeekerDao.findByEmail(email);
    }

    @Override
    public List<JobSeeker> getAllJobSeekers() {
        return jobSeekerDao.findAll();
    }

    @Override
    public void deleteJobSeeker(int id) {
        jobSeekerDao.deleteById(id);
    }
    @Override
    public void saveApplication(JobApplication application) {
        jobApplicationDao.save(application);
    }
    @Override
    public List<JobApplication> getApplicationsByJobSeeker(JobSeeker jobSeeker) {
        try {
            List<JobApplication> applications = jobApplicationDao.findByJobSeeker(jobSeeker);
            return applications != null ? applications : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public JobApplication getApplicationById(int id) {
        return jobApplicationDao.findById(id);
    }
    @Override
    public boolean hasAlreadyApplied(int jobSeekerId, int jobId) {
        return jobApplicationDao.existsByJobSeekerIdAndJobId(jobSeekerId, jobId);
    }
    @Override
    public int countTotalApplications(JobSeeker jobSeeker) {
        List<JobApplication> applications = jobApplicationDao.findByJobSeeker(jobSeeker);
        return applications != null ? applications.size() : 0;
    }

    @Override
    public int countApplicationsByStatus(JobSeeker jobSeeker, ApplicationStatus status) {
        List<JobApplication> applications = jobApplicationDao.findByJobSeekerAndStatus(jobSeeker, status);
        return applications != null ? applications.size() : 0;
    }
    
}