package com.jobboard.controller;

import com.jobboard.model.JobSeeker;
import com.jobboard.service.JobSeekerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class JobSeekerController {

    @Autowired
    private JobSeekerService jobSeekerService;

    @GetMapping("/jobseekers/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("jobSeeker", new JobSeeker());
        return "jobseeker/register";
    }

    @PostMapping("/jobseekers/register")
    public String registerJobSeeker(
            @ModelAttribute("jobSeeker") JobSeeker jobSeeker, 
            BindingResult result, 
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "jobseeker/register"; // Show form with validation errors
        }

        jobSeekerService.registerJobSeeker(jobSeeker); // Save the job seeker to the database
        redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please log in.");
        return "redirect:/jobseekers/login"; // Redirect to login page after successful registration
    }

    @GetMapping("/jobseekers/login")
    public String showLoginForm() {
        return "jobseeker/login";
    }

    @GetMapping("/jobseekers/dashboard")
    public String showDashboard() {
        // Logic to display job seeker dashboard
        return "jobseeker/dashboard";
    }
}
