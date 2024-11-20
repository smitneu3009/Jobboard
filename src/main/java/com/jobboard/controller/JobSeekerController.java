package com.jobboard.controller;

import com.jobboard.model.ApplicationStatus;
import com.jobboard.model.Job;
import com.jobboard.model.JobApplication;
import com.jobboard.model.JobSeeker;
import com.jobboard.service.JobSeekerService;
import com.jobboard.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Controller
public class JobSeekerController {

    @Autowired
    private JobSeekerService jobSeekerService;
    
    @Autowired
    private JobService jobService; // Add this

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
            return "jobseeker/register";
        }

        jobSeekerService.registerJobSeeker(jobSeeker);
        redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please log in.");
        return "redirect:/jobseekers/login";
    }

    @GetMapping("/jobseekers/login")
    public String showLoginForm() {
        return "jobseeker/login";
    }

    @GetMapping("/jobseekers/dashboard")
    public String showDashboard(
            Model model, 
            Principal principal,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double minPay,
            @RequestParam(required = false) Double maxPay,
            @RequestParam(required = false) String jobType) {
        
        String email = principal.getName();
        JobSeeker jobSeeker = jobSeekerService.findByEmail(email);
        
        // Get all jobs with filters
        List<Job> jobs = jobService.findJobsWithFilters(category, location, minPay, maxPay, jobType);
        
        // Get distinct categories and locations for filters
        List<String> categories = jobService.findAllCategories();
        List<String> locations = jobService.findAllLocations();
        List<String> jobTypes = Arrays.asList("FULL_TIME", "PART_TIME", "INTERNSHIP", "CONTRACT");

        model.addAttribute("jobSeeker", jobSeeker);
        model.addAttribute("jobs", jobs);
        model.addAttribute("categories", categories);
        model.addAttribute("locations", locations);
        model.addAttribute("jobTypes", jobTypes);
        
        // Add filter values to maintain state
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedLocation", location);
        model.addAttribute("selectedMinPay", minPay);
        model.addAttribute("selectedMaxPay", maxPay);
        model.addAttribute("selectedJobType", jobType);

        return "jobseeker/dashboard";
    }

    @GetMapping("/jobseekers/apply/{jobId}")
    public String showApplicationForm(@PathVariable int jobId, Model model, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            Job job = jobService.findById(jobId);
            if (job == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Job not found");
                return "redirect:/jobseekers/dashboard";
            }

            String email = principal.getName();
            JobSeeker jobSeeker = jobSeekerService.findByEmail(email);
            
            // Check if already applied
            if (jobSeekerService.hasAlreadyApplied(jobSeeker.getId(), jobId)) {
                redirectAttributes.addFlashAttribute("errorMessage", "You have already applied for this position");
                return "redirect:/jobseekers/dashboard";
            }

            model.addAttribute("job", job);
            model.addAttribute("jobSeeker", jobSeeker);
            return "jobseeker/apply";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error accessing job application: " + e.getMessage());
            return "redirect:/jobseekers/dashboard";
        }
    }

    @PostMapping("/jobseekers/apply/{jobId}")
    public String submitApplication(
            @PathVariable int jobId,
            @RequestParam("coverLetter") String coverLetter,
            @RequestParam("resume") MultipartFile resume,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            // Validate file
            if (resume.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Please select a resume file");
                return "redirect:/jobseekers/apply/" + jobId;
            }

            // Validate file type
            String originalFilename = resume.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            if (!Arrays.asList("pdf", "doc", "docx").contains(fileExtension)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Invalid file type. Please upload PDF or DOC files");
                return "redirect:/jobseekers/apply/" + jobId;
            }

            String email = principal.getName();
            JobSeeker jobSeeker = jobSeekerService.findByEmail(email);
            
            // Save resume file
            String fileName = UUID.randomUUID().toString() + "_" + originalFilename;
            String uploadDir = "uploads/resumes/";
            Path uploadPath = Paths.get(uploadDir);
            
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(resume.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Create application
            JobApplication application = new JobApplication();
            application.setJob(jobService.findById(jobId));
            application.setJobSeeker(jobSeeker);
            application.setCoverLetter(coverLetter);
            application.setResumePath(uploadDir + fileName);
            application.setApplicationDate(LocalDateTime.now());
            application.setStatus(ApplicationStatus.PENDING);
            
            jobSeekerService.saveApplication(application);
            
            redirectAttributes.addFlashAttribute("successMessage", "Application submitted successfully!");
            return "redirect:/jobseekers/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error submitting application: " + e.getMessage());
            return "redirect:/jobseekers/apply/" + jobId;
        }
    }
    
    @GetMapping("/jobseekers/applications")
    public String showApplications(Model model, Principal principal) {
        try {
            String email = principal.getName();
            JobSeeker jobSeeker = jobSeekerService.findByEmail(email);
            List<JobApplication> jobApplications = jobSeekerService.getApplicationsByJobSeeker(jobSeeker);
            
            // Initialize empty list if null
            if (jobApplications == null) {
                jobApplications = new ArrayList<>();
            }
            
            model.addAttribute("jobApplications", jobApplications); // Changed from "applications" to "jobApplications"
            model.addAttribute("jobSeeker", jobSeeker);
            return "jobseeker/applications";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading applications: " + e.getMessage());
            return "jobseeker/applications";
        }
    }

    @GetMapping("/jobseekers/applications/resume/{id}")
    public ResponseEntity<Resource> downloadResume(@PathVariable("id") int applicationId, Principal principal) {
        try {
            String email = principal.getName();
            JobSeeker jobSeeker = jobSeekerService.findByEmail(email);
            JobApplication jobApplication = jobSeekerService.getApplicationById(applicationId);
            
            // Security check using == for primitive int comparison
            if (jobApplication.getJobSeeker().getId() != jobSeeker.getId()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Path resumePath = Paths.get(jobApplication.getResumePath());
            Resource resource = new UrlResource(resumePath.toUri());

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
