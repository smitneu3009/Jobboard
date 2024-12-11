package com.jobboard.controller;

import com.jobboard.model.ApplicationStatus;
import com.jobboard.model.Company;
import com.jobboard.model.Job;
import com.jobboard.model.JobApplication;
import com.jobboard.service.CompanyService;
import com.jobboard.service.EmailService;
import com.jobboard.service.JobApplicationService;
import com.jobboard.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private JobService jobService;
    
    @Autowired
    private JobApplicationService jobApplicationService;
    
    @Autowired
    private EmailService emailService;

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
        try {
            String email = principal.getName();
            Company company = companyService.findByEmail(email);
            Job job = jobService.findById(id);
            
            // Verify that the job belongs to the company
            if (job == null || job.getCompany().getId() != company.getId()) {
                return "redirect:/company/dashboard";
            }
            
            model.addAttribute("company", company);
            model.addAttribute("job", job);
            return "company/edit-job";  // Make sure this template exists
        } catch (Exception e) {
            return "redirect:/company/dashboard";
        }
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
    
    @GetMapping("/company/profile")
    public String showProfile(Model model, Principal principal) {
        String email = principal.getName();
        Company company = companyService.findByEmail(email);
        
        // Get statistics
        List<Job> companyJobs = jobService.findByCompany(company);
        int totalJobs = companyJobs.size();
        int totalApplications = jobService.countTotalApplicationsByCompany(company);
        
        model.addAttribute("company", company);
        model.addAttribute("totalJobs", totalJobs);
        model.addAttribute("totalApplications", totalApplications);
        
        return "company/profile";
    }
    @GetMapping("/company/profile/edit")
    public String showEditProfileForm(Model model, Principal principal) {
        String email = principal.getName();
        Company company = companyService.findByEmail(email);
        model.addAttribute("company", company);
        return "company/edit-profile";
    }

    @PostMapping("/company/profile/update")
    public String updateProfile(
            @ModelAttribute Company company,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            String email = principal.getName();
            Company existingCompany = companyService.findByEmail(email);
            
            // Update only allowed fields
            existingCompany.setCompanyName(company.getCompanyName());
            existingCompany.setCategory(company.getCategory());
            existingCompany.setLocation(company.getLocation());
            existingCompany.setDescription(company.getDescription());
            
            companyService.updateCompany(existingCompany);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating profile: " + e.getMessage());
        }
        return "redirect:/company/profile";
    }
    @GetMapping("/company/job/applicants/{jobId}")
    public String viewApplicants(@PathVariable int jobId, Model model, Principal principal) {
        try {
            String email = principal.getName();
            Company company = companyService.findByEmail(email);
            Job job = jobService.findById(jobId);
            
            if (job == null || job.getCompany().getId() != company.getId()) {
                return "redirect:/company/dashboard";
            }
            
            List<JobApplication> jobApplications = jobApplicationService.findByJobId(jobId);
            if (jobApplications == null) {
                jobApplications = new ArrayList<>();
            }
            
            model.addAttribute("company", company);
            model.addAttribute("job", job);
            model.addAttribute("jobApplications", jobApplications);
            
            return "company/applicants";
        } catch (Exception e) {
            return "redirect:/company/dashboard";
        }
    }
    

    @PostMapping("/company/applications/status/{id}/{status}")
    public String updateApplicationStatus(
            @PathVariable int id,
            @PathVariable ApplicationStatus status,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            JobApplication application = jobApplicationService.findById(id);
            String email = principal.getName();
            Company company = companyService.findByEmail(email);
            
            if (application.getJob().getCompany().getId() != company.getId()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized access");
                return "redirect:/company/dashboard";
            }
            
            ApplicationStatus oldStatus = application.getStatus();
            application.setStatus(status);
            jobApplicationService.save(application);
            
            // Send email notification
            emailService.sendStatusUpdateEmail(application, oldStatus, status);
            
            redirectAttributes.addFlashAttribute("successMessage", "Application status updated successfully");
            return "redirect:/company/job/applicants/" + application.getJob().getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating status: " + e.getMessage());
            return "redirect:/company/dashboard";
        }
    }
    @GetMapping("/company/applications/resume/{id}")
    public ResponseEntity<Resource> downloadResume(@PathVariable int id, Principal principal) {
        try {
            String email = principal.getName();
            Company company = companyService.findByEmail(email);
            JobApplication application = jobApplicationService.findById(id);
            
            // Security check - ensure company owns this application
            if (application.getJob().getCompany().getId() != company.getId()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // Get the resume file
            Path resumePath = Paths.get(application.getResumePath());
            Resource resource = new UrlResource(resumePath.toUri());
            
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            // Set headers for file download
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + 
                       resource.getFilename() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}