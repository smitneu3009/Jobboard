package com.jobboard.serviceImpl;

import com.jobboard.dao.JobDao;
import com.jobboard.model.Job;
import com.jobboard.model.Company;
import com.jobboard.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private JobDao jobDao;

    @Override
    public void saveJob(Job job) {
        jobDao.save(job);
    }

    @Override
    public void updateJob(Job job) {
        jobDao.update(job);
    }

    @Override
    public void deleteJob(int id) {
        jobDao.delete(id);
    }

    @Override
    public Job findById(int id) {
        return jobDao.findById(id);
    }

    @Override
    public List<Job> findByCompany(Company company) {
        return jobDao.findByCompany(company);
    }
    @Override
    public int countActiveJobsByCompany(Company company) {
        return jobDao.countActiveJobsByCompany(company);
    }

    @Override
    public int countTotalApplicationsByCompany(Company company) {
        return jobDao.countTotalApplicationsByCompany(company);
    }
}