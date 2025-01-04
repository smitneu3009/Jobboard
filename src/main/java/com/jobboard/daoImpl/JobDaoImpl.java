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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;


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
            
            String deleteApplicationsHql = "DELETE FROM JobApplication WHERE job.id = :jobId";
            session.createQuery(deleteApplicationsHql)
                .setParameter("jobId", id)
                .executeUpdate();
                
            String deleteJobHql = "DELETE FROM Job WHERE id = :jobId";
            session.createQuery(deleteJobHql)
                .setParameter("jobId", id)
                .executeUpdate();
                
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Error deleting job: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    @Override
    public Job findById(int id) {
        Session session = sessionFactory.openSession();
        try {
            String hql = "FROM Job j LEFT JOIN FETCH j.company WHERE j.id = :id";
            return session.createQuery(hql, Job.class)
                    .setParameter("id", id)
                    .uniqueResult();
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
        return 0;
    }
    
    @Override
    public List<Job> findJobsWithFilters(String category, String location, Double minPay, Double maxPay, String jobType) {
        Session session = sessionFactory.openSession();
        try {
            StringBuilder hql = new StringBuilder("FROM Job j WHERE 1=1");
            Map<String, Object> params = new HashMap<>(); 

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
    @Override
    public List<Job> findAll() {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery("FROM Job", Job.class).list();
        } finally {
            session.close();
        }
    }
    @Override
    public Page<Job> findAllPaginated(Pageable pageable) {
        Session session = sessionFactory.openSession();
        try {
            Long total = session.createQuery("SELECT COUNT(j) FROM Job j", Long.class)
                .getSingleResult();

            List<Job> jobs = session.createQuery("FROM Job j JOIN FETCH j.company", Job.class)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

            return new PageImpl<>(jobs, pageable, total);
        } finally {
            session.close();
        }
    }
    @Override
    public List<Job> findByCompanyId(int companyId) {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery(
                "SELECT j FROM Job j WHERE j.company.id = :companyId", 
                Job.class)
                .setParameter("companyId", companyId)
                .getResultList();
        } finally {
            session.close();
        }
    }

    @Override
    public Page<Job> findJobsWithFiltersAndPagination(
            String searchTerm,
            String category,
            String location,
            Double minPay,
            Double maxPay,
            String jobType,
            Pageable pageable) {
        
        Session session = sessionFactory.openSession();
        try {
            String hql = "FROM Job j WHERE 1=1";
            Map<String, Object> params = new HashMap<>();

            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                hql += " AND (LOWER(j.title) LIKE :searchTerm OR " +
                       "LOWER(j.description) LIKE :searchTerm OR " +
                       "LOWER(j.location) LIKE :searchTerm OR " +
                       "LOWER(j.jobType) LIKE :searchTerm OR " +
                       "LOWER(j.company.companyName) LIKE :searchTerm)";
                params.put("searchTerm", "%" + searchTerm.toLowerCase() + "%");
            }
            if (location != null && !location.isEmpty()) {
                hql += " AND j.location = :location";
                params.put("location", location);
            }

            if (minPay != null) {
                hql += " AND j.payPerHour >= :minPay";
                params.put("minPay", minPay);
            }

            if (jobType != null && !jobType.isEmpty()) {
                hql += " AND j.jobType = :jobType";
                params.put("jobType", jobType);
            }

            Query<Job> query = session.createQuery(hql, Job.class);
            params.forEach(query::setParameter);

            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());

            String countHql = "SELECT COUNT(j) " + hql;
            Query<Long> countQuery = session.createQuery(countHql, Long.class);
            params.forEach(countQuery::setParameter);
            Long total = countQuery.getSingleResult();

            List<Job> jobs = query.getResultList();
            return new PageImpl<>(jobs, pageable, total);
        } finally {
            session.close();
        }
    }


    
    
}