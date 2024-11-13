package com.jobboard.daoImpl;

import com.jobboard.dao.JobSeekerDao;
import com.jobboard.model.JobSeeker;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class JobSeekerDaoImpl implements JobSeekerDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void applyForJob(JobSeeker jobSeeker) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.save(jobSeeker);
        tx.commit();
        session.close();
    }

    @Override
    public JobSeeker getProfile(int id) {
        Session session = sessionFactory.openSession();
        JobSeeker jobSeeker = session.get(JobSeeker.class, id);
        session.close();
        return jobSeeker;
    }

    @Override
    public void updateProfile(JobSeeker jobSeeker) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.update(jobSeeker);
        tx.commit();
        session.close();
    }

    @Override
    public void registerJobSeeker(JobSeeker jobSeeker) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.save(jobSeeker);
        tx.commit();
        session.close();
    }
    @Override
    public JobSeeker findByEmail(String email) {
        System.out.println("Searching for JobSeeker with email: " + email);
        Session session = sessionFactory.openSession();
        try {
            String hql = "FROM JobSeeker WHERE email = :email";
            JobSeeker jobSeeker = session.createQuery(hql, JobSeeker.class)
                    .setParameter("email", email)
                    .uniqueResult();
            System.out.println("JobSeeker found: " + (jobSeeker != null ? jobSeeker.getEmail() : "null"));
            return jobSeeker;
        } finally {
            session.close();
        }
    }
}
