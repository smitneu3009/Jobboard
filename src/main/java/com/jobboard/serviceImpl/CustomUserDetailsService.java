package com.jobboard.serviceImpl;

import com.jobboard.dao.JobSeekerDao;
import com.jobboard.model.JobSeeker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private JobSeekerDao jobSeekerDao;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("Attempting to load user with email: " + email);
        if (email == null || email.trim().isEmpty()) {
            throw new UsernameNotFoundException("Email cannot be empty");
        }

        JobSeeker jobSeeker = jobSeekerDao.findByEmail(email.trim());
        if (jobSeeker == null) {
            System.out.println("User not found for email: " + email);
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        System.out.println("JobSeeker found: " + jobSeeker.getEmail());
        return User.builder()
                .username(jobSeeker.getEmail())
                .password(jobSeeker.getPassword())
                .roles("JOBSEEKER")
                .build();
    }
}