package com.jobboard.serviceImpl;

import com.jobboard.dao.AdminDao;
import com.jobboard.dao.CompanyDao;
import com.jobboard.dao.JobSeekerDao;
import com.jobboard.model.Admin;
import com.jobboard.model.Company;
import com.jobboard.model.JobSeeker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private JobSeekerDao jobSeekerDao;
    
    @Autowired
    private AdminDao adminDao;
    
    @Autowired
    private CompanyDao companyDao;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Try admin first
        Admin admin = adminDao.findByAdminEmail(email);
        if (admin != null) {
            return new User(
                admin.getEmail(),
                admin.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }

        // Try company
        Company company = companyDao.findByEmail(email);
        if (company != null) {
            return new User(
                company.getEmail(),
                company.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_COMPANY"))
            );
        }

        // Try jobseeker
        JobSeeker jobSeeker = jobSeekerDao.findByEmail(email);
        if (jobSeeker != null) {
            return new User(
                jobSeeker.getEmail(),
                jobSeeker.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_JOBSEEKER"))
            );
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}