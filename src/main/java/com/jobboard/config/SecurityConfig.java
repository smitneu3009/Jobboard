package com.jobboard.config;

import com.jobboard.serviceImpl.CustomUserDetailsService;
import com.jobboard.util.CustomPasswordEncoder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
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
    @Order(2)
    public SecurityFilterChain jobseekerSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/jobseekers/**")
                .authorizeHttpRequests(authorize -> authorize
                                .requestMatchers("/jobseekers/register", "/jobseekers/login").permitAll()
                                .anyRequest().hasRole("JOBSEEKER")
                )
                .formLogin(form -> form
                                .loginPage("/jobseekers/login")
                                .loginProcessingUrl("/jobseekers/login")
                                .defaultSuccessUrl("/jobseekers/dashboard", true)
                                .failureUrl("/jobseekers/login?error=true")
                                .failureHandler((request, response, exception) -> {
                                    String errorMessage = "Invalid email or password";
                                    request.getSession().setAttribute("error", errorMessage);
                                    response.sendRedirect("/jobseekers/login?error=true");
                                })
                                .permitAll()
                )
                .logout(logout -> logout
                                .logoutUrl("/jobseekers/logout")
                                .logoutSuccessUrl("/")
                                .invalidateHttpSession(true)
                                .deleteCookies("JSESSIONID")
                )
                .exceptionHandling(exception -> exception
                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                    if (request.isUserInRole("JOBSEEKER")) {
                                        response.sendRedirect("/jobseekers/dashboard");
                                    } else {
                                        response.sendRedirect("/jobseekers/login");
                                    }
                                })
                                .authenticationEntryPoint((request, response, authException) -> {
                                    response.sendRedirect("/jobseekers/login");
                                })
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
    
    @Bean
    @Order(1)
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/admin/**")
                .authorizeHttpRequests(authorize -> authorize
                                .requestMatchers("/admin/login").permitAll()
                                .anyRequest().hasRole("ADMIN")
                )
                .formLogin(form -> form
                                .loginPage("/admin/login")
                                .loginProcessingUrl("/admin/login")
                                .defaultSuccessUrl("/admin/dashboard", true)
                                .failureUrl("/admin/login?error=true")
                                .failureHandler((request, response, exception) -> {
                                    String errorMessage = "Invalid email or password";
                                    request.getSession().setAttribute("error", errorMessage);
                                    response.sendRedirect("/admin/login?error=true");
                                })
                                .permitAll()
                )
                .logout(logout -> logout
                                .logoutUrl("/admin/logout")
                                .logoutSuccessUrl("/admin/login")
                                .invalidateHttpSession(true)
                                .deleteCookies("JSESSIONID")
                )
                .exceptionHandling(exception -> exception
                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                    if (request.isUserInRole("ADMIN")) {
                                        response.sendRedirect("/admin/dashboard");
                                    } else {
                                        response.sendRedirect("/admin/login");
                                    }
                                })
                                .authenticationEntryPoint((request, response, authException) -> {
                                    response.sendRedirect("/admin/login");
                                })
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

   
    
    @Bean
    @Order(3)
    public SecurityFilterChain companySecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/company/**")
                .authorizeHttpRequests(authorize -> authorize
                                .requestMatchers("/company/register", "/company/login").permitAll()
                                .anyRequest().hasRole("COMPANY")
                )
                .formLogin(form -> form
                                .loginPage("/company/login")
                                .loginProcessingUrl("/company/login")
                                .defaultSuccessUrl("/company/dashboard", true)
                                .failureUrl("/company/login?error=true")
                                .failureHandler((request, response, exception) -> {
                                    String errorMessage = "Invalid email or password";
                                    request.getSession().setAttribute("error", errorMessage);
                                    response.sendRedirect("/company/login?error=true");
                                })
                                .permitAll()
                )
                .logout(logout -> logout
                                .logoutUrl("/company/logout")
                                .logoutSuccessUrl("/")
                                .invalidateHttpSession(true)
                                .deleteCookies("JSESSIONID")
                )
                .exceptionHandling(exception -> exception
                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                    if (request.isUserInRole("COMPANY")) {
                                        response.sendRedirect("/company/dashboard");
                                    } else {
                                        response.sendRedirect("/company/login");
                                    }
                                })
                                .authenticationEntryPoint((request, response, authException) -> {
                                    response.sendRedirect("/company/login");
                                })
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
    @Bean
    @Order(4)
    public SecurityFilterChain publicSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/", "/homepage")
                .authorizeHttpRequests(authorize -> authorize
                                .requestMatchers("/", "/homepage").permitAll()
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/error")
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new CustomPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}