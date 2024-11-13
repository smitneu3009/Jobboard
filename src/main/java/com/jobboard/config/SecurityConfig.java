package com.jobboard.config;

import com.jobboard.serviceImpl.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .authorizeRequests()
        .requestMatchers("/jobseekers/register", "/jobseekers/login").permitAll()  // Allow access to registration and login pages
        .anyRequest().authenticated()  // All other pages require authentication
    .and()
    .formLogin()
        .loginPage("/jobseekers/login")  // Custom login page
        .defaultSuccessUrl("/jobseekers/dashboard", true)  // Redirect to dashboard upon successful login
        .failureUrl("/jobseekers/login?error=true")  // Redirect to login page with error message on failure
        .permitAll()  // Allow everyone to access the login page
    .and()
    .logout()
        .logoutUrl("/logout")
        .logoutSuccessUrl("/jobseekers/login?logout=true")
        .permitAll()
            .and()
            .csrf().disable();  // Disable CSRF for simplicity; consider enabling it for production

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
