package com.jobboard.daoImpl;

import com.jobboard.dao.JobApplicationDao;
import com.jobboard.model.JobApplication;
import com.jobboard.model.JobSeeker;
import com.jobboard.model.ApplicationStatus;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
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
            if (application.getId() != 0) {
                // If ID exists, update instead of create
                session.merge(application);
            } else {
                session.save(application);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public List<JobApplication> findByJobId(int jobId) {
        Session session = sessionFactory.openSession();
        try {
            String hql = "FROM JobApplication WHERE job.id = :jobId ORDER BY applicationDate DESC";
            return session.createQuery(hql, JobApplication.class)
                    .setParameter("jobId", jobId)
                    .list();
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
    public void updateStatus(int id, ApplicationStatus status) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            JobApplication application = session.get(JobApplication.class, id);
            if (application != null) {
                application.setStatus(status);
                session.update(application);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public List<JobApplication> findByJobSeekerId(int jobSeekerId) {
        Session session = sessionFactory.openSession();
        try {
            String hql = "FROM JobApplication WHERE jobSeeker.id = :jobSeekerId ORDER BY applicationDate DESC";
            return session.createQuery(hql, JobApplication.class)
                    .setParameter("jobSeekerId", jobSeekerId)
                    .list();
        } finally {
            session.close();
        }
    }

    @Override
    public boolean existsByJobSeekerIdAndJobId(int jobSeekerId, int jobId) {
        Session session = sessionFactory.openSession();
        try {
            String hql = "SELECT COUNT(a) FROM JobApplication a WHERE a.jobSeeker.id = :jobSeekerId AND a.job.id = :jobId";
            Long count = session.createQuery(hql, Long.class)
                    .setParameter("jobSeekerId", jobSeekerId)
                    .setParameter("jobId", jobId)
                    .uniqueResult();
            return count != null && count > 0;
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
        } finally {
            session.close();
        }
    }
    @Override
    public List<JobApplication> findByJobSeekerAndStatus(JobSeeker jobSeeker, ApplicationStatus status) {
        Session session = sessionFactory.openSession();
        try {
            String hql = "FROM JobApplication WHERE jobSeeker = :jobSeeker AND status = :status";
            return session.createQuery(hql, JobApplication.class)
                    .setParameter("jobSeeker", jobSeeker)
                    .setParameter("status", status)
                    .list();
        } finally {
            session.close();
        }
    }
    @Override
    public void deleteByJobId(int jobId) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            String hql = "DELETE FROM JobApplication WHERE job.id = :jobId";
            session.createQuery(hql)
                .setParameter("jobId", jobId)
                .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}