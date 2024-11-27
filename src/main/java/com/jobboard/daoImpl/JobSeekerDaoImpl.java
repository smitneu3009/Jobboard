package com.jobboard.daoImpl;

import com.jobboard.dao.JobSeekerDao;
import com.jobboard.model.JobSeeker;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
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
            session.merge(jobSeeker); // Use merge instead of update
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
            String hql = "FROM JobSeeker WHERE email = :email";
            return session.createQuery(hql, JobSeeker.class)
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
}