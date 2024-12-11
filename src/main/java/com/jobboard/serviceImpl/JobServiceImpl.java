package com.jobboard.serviceImpl;

import com.jobboard.dao.JobApplicationDao;
import com.jobboard.dao.JobDao;
import com.jobboard.model.Job;
import com.jobboard.model.Company;
import com.jobboard.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;


@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private JobDao jobDao;
    
    @Autowired
    private JobApplicationDao jobApplicationDao;

    @Override
    public void saveJob(Job job) {
        jobDao.save(job);
    }

    @Override
    public void updateJob(Job job) {
        jobDao.update(job);
    }
    
    @Override
    public Page<Job> findAllJobs(Pageable pageable) {
        return jobDao.findAllPaginated(pageable);
    }


    @Override
    public void deleteJob(int id) {
        try {
            // First delete all applications for this job
            Job job = jobDao.findById(id);
            if (job != null) {
                // Delete all applications first
            	jobApplicationDao.deleteByJobId(id);
                // Then delete the job
                jobDao.delete(id);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deleting job: " + e.getMessage());
        }
    }

    @Override
    public Job findById(int id) {
        Job job = jobDao.findById(id);
        if (job != null) {
            // Force initialization of company
            job.getCompany().getCompanyName(); // This will trigger lazy loading
        }
        return job;
    }
    @Override
    public List<Job> findByCompanyId(int companyId) {
        return jobDao.findByCompanyId(companyId);
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
    
    @Override
    public List<Job> findJobsWithFilters(String category, String location, Double minPay, Double maxPay, String jobType) {
        return jobDao.findJobsWithFilters(category, location, minPay, maxPay, jobType);
    }

    @Override
    public List<String> findAllCategories() {
        return jobDao.findAllCategories();
    }

    @Override
    public List<String> findAllLocations() {
        return jobDao.findAllLocations();
    }
    @Override
    public List<Job> getAllJobs() {
        return jobDao.findAll();
    }
}