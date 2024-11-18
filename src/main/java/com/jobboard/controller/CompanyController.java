package com.jobboard.controller;

import com.jobboard.model.Company;
import com.jobboard.model.Job;
import com.jobboard.service.CompanyService;
import com.jobboard.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private JobService jobService;

    @GetMapping("/company/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("company", new Company());
        return "company/register";
    }

    @PostMapping("/company/register")
    public String registerCompany(
            @ModelAttribute("company") Company company,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "company/register";
        }

        companyService.registerCompany(company);
        redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please log in.");
        return "redirect:/company/login";
    }

    @GetMapping("/company/login")
    public String showLoginForm() {
        return "company/login";
    }

    @GetMapping("/company/dashboard")
    public String showDashboard(Model model, Principal principal) {
        String email = principal.getName();
        Company company = companyService.findByEmail(email);
        List<Job> jobs = jobService.findByCompany(company);
        
        model.addAttribute("company", company);
        model.addAttribute("jobs", jobs);
        return "company/dashboard";
    }

    @GetMapping("/company/job/create")
    public String showCreateJobForm(Model model) {
        model.addAttribute("job", new Job());
        return "company/create-job";
    }

    @PostMapping("/company/job/create")
    public String createJob(@ModelAttribute Job job, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            String email = principal.getName();
            Company company = companyService.findByEmail(email);
            job.setCompany(company);
            jobService.saveJob(job);
            redirectAttributes.addFlashAttribute("successMessage", "Job posting created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating job posting: " + e.getMessage());
        }
        return "redirect:/company/dashboard";
    }

    @GetMapping("/company/job/edit/{id}")
    public String showEditJobForm(@PathVariable("id") int id, Model model, Principal principal) {
        String email = principal.getName();
        Company company = companyService.findByEmail(email);
        Job job = jobService.findById(id);
        
        // Verify that the job belongs to the company using == for primitive int
        if (job == null || job.getCompany().getId() != company.getId()) {
            return "redirect:/company/dashboard";
        }
        
        model.addAttribute("job", job);
        return "company/edit-job";
    }

    @PostMapping("/company/job/update")
    public String updateJob(@ModelAttribute Job job, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            String email = principal.getName();
            Company company = companyService.findByEmail(email);
            
            // Verify ownership before updating using == for primitive int
            Job existingJob = jobService.findById(job.getId());
            if (existingJob != null && existingJob.getCompany().getId() == company.getId()) {
                job.setCompany(company);
                jobService.updateJob(job);
                redirectAttributes.addFlashAttribute("successMessage", "Job posting updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized to update this job posting.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating job posting: " + e.getMessage());
        }
        return "redirect:/company/dashboard";
    }

    @GetMapping("/company/job/delete/{id}")
    public String deleteJob(@PathVariable("id") int id, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            String email = principal.getName();
            Company company = companyService.findByEmail(email);
            
            // Verify ownership before deleting using == for primitive int
            Job job = jobService.findById(id);
            if (job != null && job.getCompany().getId() == company.getId()) {
                jobService.deleteJob(id);
                redirectAttributes.addFlashAttribute("successMessage", "Job posting deleted successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized to delete this job posting.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting job posting: " + e.getMessage());
        }
        return "redirect:/company/dashboard";
    }
    
}