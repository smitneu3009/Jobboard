package com.jobboard.daoImpl;

import com.jobboard.dao.JobDao;
import com.jobboard.model.Job;

import org.hibernate.query.Query;
import com.jobboard.model.Company;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
            String hql = "FROM Job j LEFT JOIN FETCH j.applications WHERE j.company = :company";
            return session.createQuery(hql, Job.class)
                    .setParameter("company", company)
                    .list();
        } finally {
            session.close();
        }
    }
    @Override
    public int countActiveJobsByCompany(Company company) {
        Session session = sessionFactory.openSession();
        try {
            String hql = "SELECT COUNT(j) FROM Job j WHERE j.company = :company";
            return session.createQuery(hql, Long.class)
                    .setParameter("company", company)
                    .uniqueResult()
                    .intValue();
        } finally {
            session.close();
        }
    }

    @Override
    public int countTotalApplicationsByCompany(Company company) {
        // Implement this when you add job applications functionality
        return 0;
    }
    
    @Override
    public List<Job> findJobsWithFilters(String category, String location, Double minPay, Double maxPay, String jobType) {
        Session session = sessionFactory.openSession();
        try {
            StringBuilder hql = new StringBuilder("FROM Job j WHERE 1=1");
            Map<String, Object> params = new HashMap<>();  // Now HashMap is properly imported

            if (category != null && !category.isEmpty()) {
                hql.append(" AND j.company.category = :category");
                params.put("category", category);
            }

            if (location != null && !location.isEmpty()) {
                hql.append(" AND j.location = :location");
                params.put("location", location);
            }

            if (minPay != null) {
                hql.append(" AND j.payPerHour >= :minPay");
                params.put("minPay", minPay);
            }

            if (maxPay != null) {
                hql.append(" AND j.payPerHour <= :maxPay");
                params.put("maxPay", maxPay);
            }

            if (jobType != null && !jobType.isEmpty()) {
                hql.append(" AND j.jobType = :jobType");
                params.put("jobType", jobType);
            }

            // Use org.hibernate.query.Query instead of jakarta.persistence.Query
            Query<Job> query = session.createQuery(hql.toString(), Job.class);
            params.forEach((key, value) -> query.setParameter(key, value));
            return query.list();
        } finally {
            session.close();
        }
    }


    @Override
    public List<String> findAllCategories() {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery(
                "SELECT DISTINCT c.category FROM Company c", String.class)
                .list();
        } finally {
            session.close();
        }
    }

    @Override
    public List<String> findAllLocations() {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery(
                "SELECT DISTINCT j.location FROM Job j", String.class)
                .list();
        } finally {
            session.close();
        }
    }
    
}