package com.jobboard.controller;

import com.jobboard.dao.CompanyDao;
import com.jobboard.dao.JobApplicationDao;
import com.jobboard.dao.JobDao;
import com.jobboard.dao.JobSeekerDao;
import com.jobboard.model.Company;
import com.jobboard.model.Job;
import com.jobboard.model.JobSeeker;

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
    private JobSeekerDao jobSeekerDao;
	
	@Autowired
	private JobDao jobDao;
	
	@Autowired
	private CompanyDao companyDao;
	
	@Autowired
	private JobApplicationDao jobApplicationDao;

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
        Page<JobSeeker> jobSeekerPage = jobSeekerDao.findAllPaginated(pageable);
        
        model.addAttribute("jobSeekers", jobSeekerPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", jobSeekerPage.getTotalPages());
        model.addAttribute("totalItems", jobSeekerPage.getTotalElements());
        
        return "admin/dashboard";
    }

    @GetMapping("/admin/jobseeker/edit/{id}")
    public String showEditJobSeekerForm(@PathVariable("id") int id, Model model) {
        try {
            JobSeeker jobSeeker = jobSeekerDao.getProfile(id);
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
            JobSeeker existingJobSeeker = jobSeekerDao.getProfile(jobSeeker.getId());
            
            existingJobSeeker.setFirstName(jobSeeker.getFirstName());
            existingJobSeeker.setLastName(jobSeeker.getLastName());
            existingJobSeeker.setEmail(jobSeeker.getEmail());
            
            if (jobSeeker.getPassword() != null && !jobSeeker.getPassword().trim().isEmpty()) {
                existingJobSeeker.setPassword(jobSeeker.getPassword());
            }
            
            jobSeekerDao.updateProfile(existingJobSeeker);
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
    	jobSeekerDao.deleteById(id);
        return "redirect:/admin/dashboard";
    }
    @GetMapping("/admin/jobs")
    public String showJobs(Model model, 
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Job> jobPage = jobDao.findAllPaginated(pageable);
        
        model.addAttribute("jobs", jobPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", jobPage.getTotalPages());
        model.addAttribute("totalItems", jobPage.getTotalElements());
        
        return "admin/jobs";
    }


    @GetMapping("/admin/job/edit/{id}")
    public String showEditJobForm(@PathVariable("id") int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Job job = jobDao.findById(id);
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
            Job existingJob = jobDao.findById(job.getId());
            
            existingJob.setTitle(job.getTitle());
            existingJob.setDescription(job.getDescription());
            existingJob.setLocation(job.getLocation());
            existingJob.setJobType(job.getJobType());
            existingJob.setPayPerHour(job.getPayPerHour());
            
            jobDao.update(existingJob);
            redirectAttributes.addFlashAttribute("successMessage", "Job updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating job: " + e.getMessage());
        }
        return "redirect:/admin/jobs";
    }

    @GetMapping("/admin/job/delete/{id}")
    public String deleteJob(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        try {
        	jobDao.delete(id);
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
        Page<Company> companyPage = companyDao.findAllPaginated(pageable);
        
        model.addAttribute("companies", companyPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", companyPage.getTotalPages());
        model.addAttribute("totalItems", companyPage.getTotalElements());
        
        return "admin/companies";
    }
    @GetMapping("/admin/company/delete/{id}")
    public String deleteCompany(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        try {
            List<Job> companyJobs = jobDao.findByCompanyId(id);
            
            for (Job job : companyJobs) {
                jobApplicationDao.deleteByJobId(job.getId());
                
                jobDao.delete(job.getId());
            }
            
            companyDao.deleteById(id);
            
            redirectAttributes.addFlashAttribute("successMessage", "Company and all associated data deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting company: " + e.getMessage());
        }
        return "redirect:/admin/companies";
    }


}