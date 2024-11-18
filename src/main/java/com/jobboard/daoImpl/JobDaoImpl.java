package com.jobboard.daoImpl;

import com.jobboard.dao.JobDao;
import com.jobboard.model.Job;
import com.jobboard.model.Company;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class JobDaoImpl implements JobDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(Job job) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(job);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public void update(Job job) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.update(job);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public void delete(int id) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Job job = session.get(Job.class, id);
            if (job != null) {
                session.delete(job);
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
    public Job findById(int id) {
        Session session = sessionFactory.openSession();
        try {
            return session.get(Job.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public List<Job> findByCompany(Company company) {
        Session session = sessionFactory.openSession();
        try {
            String hql = "FROM Job WHERE company = :company";
            return session.createQuery(hql, Job.class)
                    .setParameter("company", company)
                    .list();
        } finally {
            session.close();
        }
    }
    
}