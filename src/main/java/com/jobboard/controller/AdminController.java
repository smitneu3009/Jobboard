package com.jobboard.controller;

import com.jobboard.model.Company;
import com.jobboard.model.Job;
import com.jobboard.model.JobSeeker;
import com.jobboard.service.CompanyService;
import com.jobboard.service.JobSeekerService;
import com.jobboard.service.JobService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class AdminController {
	@Autowired
    private JobSeekerService jobSeekerService;
	
	@Autowired
    private JobService jobService;
	
	@Autowired
	private CompanyService companyService;

    @GetMapping("/admin/login")
    public String showAdminLoginForm() {
        return "admin/login";
    }

    @GetMapping("/admin/dashboard")
    public String showAdminDashboard(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<JobSeeker> jobSeekerPage = jobSeekerService.getAllJobSeekersPageable(pageable);
        
        model.addAttribute("jobSeekers", jobSeekerPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", jobSeekerPage.getTotalPages());
        model.addAttribute("totalItems", jobSeekerPage.getTotalElements());
        
        return "admin/dashboard";
    }



    @GetMapping("/admin/jobseeker/edit/{id}")
    public String showEditJobSeekerForm(@PathVariable("id") int id, Model model) {
        try {
            JobSeeker jobSeeker = jobSeekerService.getProfile(id);
            if (jobSeeker == null) {
                throw new RuntimeException("Job Seeker not found");
            }
            model.addAttribute("jobSeeker", jobSeeker);
            return "admin/edit-jobseeker";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading job seeker: " + e.getMessage());
            return "redirect:/admin/dashboard";
        }
    }

    @PostMapping("/admin/jobseeker/update")
    public String updateJobSeeker(
            @ModelAttribute JobSeeker jobSeeker,
            RedirectAttributes redirectAttributes) {
        try {
            // Get existing jobSeeker to preserve password if not changed
            JobSeeker existingJobSeeker = jobSeekerService.getProfile(jobSeeker.getId());
            
            // Update fields
            existingJobSeeker.setFirstName(jobSeeker.getFirstName());
            existingJobSeeker.setLastName(jobSeeker.getLastName());
            existingJobSeeker.setEmail(jobSeeker.getEmail());
            
            // Only update password if provided
            if (jobSeeker.getPassword() != null && !jobSeeker.getPassword().trim().isEmpty()) {
                existingJobSeeker.setPassword(jobSeeker.getPassword());
            }
            
            jobSeekerService.updateProfile(existingJobSeeker);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Job seeker profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error updating profile: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/admin/jobseeker/delete/{id}")
    public String deleteJobSeeker(@PathVariable("id") int id) {
        jobSeekerService.deleteJobSeeker(id);
        return "redirect:/admin/dashboard";
    }
    @GetMapping("/admin/jobs")
    public String showJobs(Model model, 
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Job> jobPage = jobService.findAllJobs(pageable);
        
        model.addAttribute("jobs", jobPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", jobPage.getTotalPages());
        model.addAttribute("totalItems", jobPage.getTotalElements());
        
        return "admin/jobs";
    }


    @GetMapping("/admin/job/edit/{id}")
    public String showEditJobForm(@PathVariable("id") int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Job job = jobService.findById(id);
            if (job == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Job not found");
                return "redirect:/admin/jobs";
            }
            model.addAttribute("job", job);
            return "admin/edit-job";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error loading job: " + e.getMessage());
            return "redirect:/admin/jobs";
        }
    }

    @PostMapping("/admin/job/update")
    public String updateJob(@ModelAttribute Job job, RedirectAttributes redirectAttributes) {
        try {
            // Get existing job to preserve company relationship
            Job existingJob = jobService.findById(job.getId());
            
            // Update fields
            existingJob.setTitle(job.getTitle());
            existingJob.setDescription(job.getDescription());
            existingJob.setLocation(job.getLocation());
            existingJob.setJobType(job.getJobType());
            existingJob.setPayPerHour(job.getPayPerHour());
            
            jobService.updateJob(existingJob);
            redirectAttributes.addFlashAttribute("successMessage", "Job updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating job: " + e.getMessage());
        }
        return "redirect:/admin/jobs";
    }

    @GetMapping("/admin/job/delete/{id}")
    public String deleteJob(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        try {
            jobService.deleteJob(id);
            redirectAttributes.addFlashAttribute("successMessage", "Job deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting job: " + e.getMessage());
        }
        return "redirect:/admin/jobs";
    }
    
    @GetMapping("/admin/companies")
    public String showCompanies(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Company> companyPage = companyService.getAllCompaniesPageable(pageable);
        
        model.addAttribute("companies", companyPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", companyPage.getTotalPages());
        model.addAttribute("totalItems", companyPage.getTotalElements());
        
        return "admin/companies";
    }
    @GetMapping("/admin/company/delete/{id}")
    public String deleteCompany(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        try {
            // First delete all jobs associated with this company
            List<Job> companyJobs = jobService.findByCompanyId(id);
            for (Job job : companyJobs) {
                jobService.deleteJob(job.getId());
            }
            
            // Then delete the company
            companyService.deleteCompany(id);
            redirectAttributes.addFlashAttribute("successMessage", "Company deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting company: " + e.getMessage());
        }
        return "redirect:/admin/companies";
    }

}