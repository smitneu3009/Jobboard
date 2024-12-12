package com.jobboard.daoImpl;

import com.jobboard.dao.JobSeekerDao;
import com.jobboard.model.ApplicationStatus;
import com.jobboard.model.JobApplication;
import com.jobboard.model.JobSeeker;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class JobSeekerDaoImpl implements JobSeekerDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void applyForJob(JobSeeker jobSeeker) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            session.save(jobSeeker);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public JobSeeker getProfile(int id) {
        Session session = sessionFactory.openSession();
        try {
            return session.get(JobSeeker.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public void updateProfile(JobSeeker jobSeeker) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            session.merge(jobSeeker);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public void registerJobSeeker(JobSeeker jobSeeker) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            session.save(jobSeeker);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public JobSeeker findByEmail(String email) {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery("FROM JobSeeker WHERE email = :email", JobSeeker.class)
                    .setParameter("email", email)
                    .uniqueResult();
        } finally {
            session.close();
        }
    }

    @Override
    public List<JobSeeker> findAll() {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery("FROM JobSeeker", JobSeeker.class).list();
        } finally {
            session.close();
        }
    }

    @Override
    public void deleteById(int id) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            JobSeeker jobSeeker = session.get(JobSeeker.class, id);
            if (jobSeeker != null) {
                session.delete(jobSeeker);
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
    public Page<JobSeeker> findAllPaginated(Pageable pageable) {
        Session session = sessionFactory.openSession();
        try {
            Long total = session.createQuery("SELECT COUNT(js) FROM JobSeeker js", Long.class)
                .getSingleResult();

            List<JobSeeker> jobSeekers = session.createQuery("FROM JobSeeker js", JobSeeker.class)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

            return new PageImpl<>(jobSeekers, pageable, total);
        } finally {
            session.close();
        }
    }

    @Override
    public void saveApplication(JobApplication application) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
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
    public List<JobApplication> findApplicationsByJobSeeker(JobSeeker jobSeeker) {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery(
                "FROM JobApplication WHERE jobSeeker = :jobSeeker", 
                JobApplication.class)
                .setParameter("jobSeeker", jobSeeker)
                .list();
        } finally {
            session.close();
        }
    }

    @Override
    public JobApplication findApplicationById(int id) {
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
            Long count = session.createQuery(
                "SELECT COUNT(a) FROM JobApplication a WHERE a.jobSeeker.id = :jobSeekerId AND a.job.id = :jobId",
                Long.class)
                .setParameter("jobSeekerId", jobSeekerId)
                .setParameter("jobId", jobId)
                .uniqueResult();
            return count != null && count > 0;
        } finally {
            session.close();
        }
    }

    @Override
    public List<JobApplication> findByJobSeekerAndStatus(JobSeeker jobSeeker, ApplicationStatus status) {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery(
                "FROM JobApplication WHERE jobSeeker = :jobSeeker AND status = :status",
                JobApplication.class)
                .setParameter("jobSeeker", jobSeeker)
                .setParameter("status", status)
                .list();
        } finally {
            session.close();
        }
    }

    @Override
    public int countTotalApplications(JobSeeker jobSeeker) {
        Session session = sessionFactory.openSession();
        try {
            Long count = session.createQuery(
                "SELECT COUNT(a) FROM JobApplication a WHERE a.jobSeeker = :jobSeeker",
                Long.class)
                .setParameter("jobSeeker", jobSeeker)
                .uniqueResult();
            return count != null ? count.intValue() : 0;
        } finally {
            session.close();
        }
    }
}