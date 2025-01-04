package com.jobboard.controller;

import com.jobboard.dao.CompanyDao;
import com.jobboard.dao.JobApplicationDao;
import com.jobboard.dao.JobDao;
import com.jobboard.model.ApplicationStatus;
import com.jobboard.model.Company;
import com.jobboard.model.Job;
import com.jobboard.model.JobApplication;
import com.jobboard.service.EmailService;
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
	private CompanyDao companyDao;

	 @Autowired
	 private JobDao jobDao; 
    
    @Autowired
    private JobApplicationDao jobApplicationDao;
    
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

        companyDao.registerCompany(company);
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
        Company company = companyDao.findByEmail(email);
        List<Job> jobs = jobDao.findByCompany(company);
        
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
            Company company = companyDao.findByEmail(email);
            job.setCompany(company);
            jobDao.save(job);
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
            Company company = companyDao.findByEmail(email);
            Job job = jobDao.findById(id);
            
            if (job == null || job.getCompany().getId() != company.getId()) {
                return "redirect:/company/dashboard";
            }
            
            model.addAttribute("company", company);
            model.addAttribute("job", job);
            return "company/edit-job";  
        } catch (Exception e) {
            return "redirect:/company/dashboard";
        }
    }

    @PostMapping("/company/job/update")
    public String updateJob(@ModelAttribute Job job, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            String email = principal.getName();
            Company company = companyDao.findByEmail(email);
            
            Job existingJob = jobDao.findById(job.getId());
            if (existingJob != null && existingJob.getCompany().getId() == company.getId()) {
                job.setCompany(company);
                jobDao.update(job);
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
            Company company = companyDao.findByEmail(email);
            
            Job job = jobDao.findById(id);
            if (job != null && job.getCompany().getId() == company.getId()) {
            	jobDao.delete(id);
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
        Company company = companyDao.findByEmail(email);
        
        List<Job> companyJobs = jobDao.findByCompany(company);
        int totalJobs = companyJobs.size();
        int totalApplications = jobDao.countTotalApplicationsByCompany(company);
        
        model.addAttribute("company", company);
        model.addAttribute("totalJobs", totalJobs);
        model.addAttribute("totalApplications", totalApplications);
        
        return "company/profile";
    }
    @GetMapping("/company/profile/edit")
    public String showEditProfileForm(Model model, Principal principal) {
        String email = principal.getName();
        Company company = companyDao.findByEmail(email);
        model.addAttribute("company", company);
        return "company/edit-profile";
    }

    @PostMapping("/company/profile/update")
    public String updateProfile(@ModelAttribute Company company, RedirectAttributes redirectAttributes) {
        try {
            Company existingCompany = companyDao.getProfile(company.getId());
            
            existingCompany.setCompanyName(company.getCompanyName());
            existingCompany.setCategory(company.getCategory());
            existingCompany.setLocation(company.getLocation());
            existingCompany.setDescription(company.getDescription());
            
            if (company.getPassword() != null && !company.getPassword().trim().isEmpty()) {
                existingCompany.setPassword(company.getPassword());
            }
            
            companyDao.updateProfile(existingCompany);
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
            Company company = companyDao.findByEmail(email);
            Job job = jobDao.findById(jobId);
            
            if (job == null || job.getCompany().getId() != company.getId()) {
                return "redirect:/company/dashboard";
            }
            
            List<JobApplication> jobApplications = jobApplicationDao.findByJobId(jobId);
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
            JobApplication application = jobApplicationDao.findById(id);
            String email = principal.getName();
            Company company = companyDao.findByEmail(email);
            
            if (application.getJob().getCompany().getId() != company.getId()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized access");
                return "redirect:/company/dashboard";
            }
            
            ApplicationStatus oldStatus = application.getStatus();
            application.setStatus(status);
            jobApplicationDao.save(application);
            
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
            Company company = companyDao.findByEmail(email);
            JobApplication application = jobApplicationDao.findById(id);
            
            if (application.getJob().getCompany().getId() != company.getId()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Path resumePath = Paths.get(application.getResumePath());
            Resource resource = new UrlResource(resumePath.toUri());
            
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

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