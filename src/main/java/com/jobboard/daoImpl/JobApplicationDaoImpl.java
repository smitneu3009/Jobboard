package com.jobboard.daoImpl;

import com.jobboard.dao.JobApplicationDao;
import com.jobboard.model.JobApplication;
import com.jobboard.model.JobSeeker;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class JobApplicationDaoImpl implements JobApplicationDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(JobApplication application) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(application);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public List<JobApplication> findByJobSeeker(JobSeeker jobSeeker) {
        Session session = sessionFactory.openSession();
        try {
            String hql = "FROM JobApplication WHERE jobSeeker = :jobSeeker ORDER BY applicationDate DESC";
            return session.createQuery(hql, JobApplication.class)
                    .setParameter("jobSeeker", jobSeeker)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // Return empty list instead of null
        } finally {
            session.close();
        }
    }

    @Override
    public JobApplication findById(int id) {
        Session session = sessionFactory.openSession();
        try {
            return session.get(JobApplication.class, id);
        } finally {
            session.close();
        }
    }
    @Override
    public boolean existsByJobSeekerIdAndJobId(int jobSeekerId, int jobId) {
        Session session = sessionFactory.openSession();
        try {
            String hql = "SELECT COUNT(ja) FROM JobApplication ja WHERE ja.jobSeeker.id = :jobSeekerId AND ja.job.id = :jobId";
            Long count = session.createQuery(hql, Long.class)
                    .setParameter("jobSeekerId", jobSeekerId)
                    .setParameter("jobId", jobId)
                    .uniqueResult();
            return count != null && count > 0;
        } finally {
            session.close();
        }
    }
}