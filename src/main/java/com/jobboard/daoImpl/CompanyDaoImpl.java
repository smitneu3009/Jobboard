package com.jobboard.daoImpl;

import com.jobboard.dao.CompanyDao;
import com.jobboard.model.Company;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@Repository
public class CompanyDaoImpl implements CompanyDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void registerCompany(Company company) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(company);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }
    
    @Override
    public void save(Company company) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.save(company);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }


    @Override
    public Company findByEmail(String email) {
        Session session = sessionFactory.openSession();
        try {
            String hql = "FROM Company WHERE email = :email";
            return session.createQuery(hql, Company.class)
                    .setParameter("email", email)
                    .uniqueResult();
        } finally {
            session.close();
        }
    }

    @Override
    public Company getProfile(int id) {
        Session session = sessionFactory.openSession();
        try {
            return session.get(Company.class, id);
        } finally {
            session.close();
        }
    }

    @Override
    public void updateProfile(Company company) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.update(company);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }
    @Override
    public void updateCompany(Company company) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.update(company);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }
   
        
        @Override
        public Page<Company> findAllPaginated(Pageable pageable) {
            Session session = sessionFactory.openSession();
            try {
                // Get total count
                Long total = session.createQuery("SELECT COUNT(c) FROM Company c", Long.class)
                    .getSingleResult();

                // Get paginated results
                List<Company> companies = session.createQuery("FROM Company c", Company.class)
                    .setFirstResult((int) pageable.getOffset())
                    .setMaxResults(pageable.getPageSize())
                    .getResultList();

                return new PageImpl<>(companies, pageable, total);
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
                Company company = session.get(Company.class, id);
                if (company != null) {
                    session.delete(company);
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