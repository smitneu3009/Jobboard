package com.jobboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class UserDetailsConfig {

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder.encode("adminpass"))
            .roles("ADMIN")
            .build();

        UserDetails company = User.builder()
            .username("company")
            .password(passwordEncoder.encode("companypass"))
            .roles("COMPANY")
            .build();

        UserDetails jobseeker = User.builder()
            .username("jobseeker")
            .password(passwordEncoder.encode("jobseekerpass"))
            .roles("JOBSEEKER")
            .build();

        return new InMemoryUserDetailsManager(admin, company, jobseeker);
    }
}