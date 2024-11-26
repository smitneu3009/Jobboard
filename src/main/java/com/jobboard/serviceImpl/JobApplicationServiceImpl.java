package com.jobboard.serviceImpl;

import com.jobboard.dao.JobApplicationDao;
import com.jobboard.model.JobApplication;
import com.jobboard.model.ApplicationStatus;
import com.jobboard.service.JobApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class JobApplicationServiceImpl implements JobApplicationService {

    @Autowired
    private JobApplicationDao jobApplicationDao;

    @Override
    public void save(JobApplication application) {
        jobApplicationDao.save(application);
    }

    @Override
    public List<JobApplication> findByJobId(int jobId) {
        return jobApplicationDao.findByJobId(jobId);
    }

    @Override
    public JobApplication findById(int id) {
        return jobApplicationDao.findById(id);
    }

    @Override
    public void updateStatus(int id, ApplicationStatus status) {
        JobApplication application = jobApplicationDao.findById(id);
        if (application != null) {
            application.setStatus(status);
            jobApplicationDao.save(application);
        }
    }

    @Override
    public List<JobApplication> findByJobSeekerId(int jobSeekerId) {
        return jobApplicationDao.findByJobSeekerId(jobSeekerId);
    }
}