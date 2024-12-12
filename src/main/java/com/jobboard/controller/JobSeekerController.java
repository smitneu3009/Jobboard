package com.jobboard.controller;

import com.jobboard.dao.JobDao;
import com.jobboard.dao.JobSeekerDao;
import com.jobboard.model.ApplicationStatus;
import com.jobboard.model.Job;
import com.jobboard.model.JobApplication;
import com.jobboard.model.JobSeeker;
import com.jobboard.service.EmailService;
//import com.jobboard.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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
    private JobSeekerDao jobSeekerDao;
    
    @Autowired
    private JobDao jobDao; // Add this
    
    
    @Autowired
    private EmailService emailService;

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

        jobSeekerDao.registerJobSeeker(jobSeeker);
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
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double minPay,
            @RequestParam(required = false) Double maxPay,
            @RequestParam(required = false) String jobType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        
        String email = principal.getName();
        JobSeeker jobSeeker = jobSeekerDao.findByEmail(email);
        
        // Create Pageable object for pagination
        Pageable pageable = PageRequest.of(page, size);
        
        // Get paginated jobs with search and filters
        Page<Job> jobPage = jobDao.findJobsWithFiltersAndPagination(
            searchTerm, category, location, minPay, maxPay, jobType, pageable);
        
        // Get distinct categories and locations for filters
        List<String> categories = jobDao.findAllCategories();
        List<String> locations = jobDao.findAllLocations();
        List<String> jobTypes = Arrays.asList("Full Time", "Part Time", "Internship", "Contract");

        // Add all necessary attributes to the model
        model.addAttribute("jobSeeker", jobSeeker);
        model.addAttribute("jobs", jobPage.getContent());
        model.addAttribute("categories", categories);
        model.addAttribute("locations", locations);
        model.addAttribute("jobTypes", jobTypes);
        
        // Add pagination attributes
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", jobPage.getTotalPages());
        model.addAttribute("totalItems", jobPage.getTotalElements());
        
        // Add filter values to maintain state
        model.addAttribute("searchTerm", searchTerm);
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
            Job job = jobDao.findById(jobId);
            if (job == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Job not found");
                return "redirect:/jobseekers/dashboard";
            }

            String email = principal.getName();
            JobSeeker jobSeeker = jobSeekerDao.findByEmail(email);
            
            // Check if already applied
            if (jobSeekerDao.existsByJobSeekerIdAndJobId(jobSeeker.getId(), jobId)) {
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
            JobSeeker jobSeeker = jobSeekerDao.findByEmail(email);
            Job job = jobDao.findById(jobId);
            
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
            application.setJob(job);
            application.setJobSeeker(jobSeeker);
            application.setCoverLetter(coverLetter);
            application.setResumePath(uploadDir + fileName);
            application.setApplicationDate(LocalDateTime.now());
            application.setStatus(ApplicationStatus.PENDING);
            
            jobSeekerDao.saveApplication(application);
            
            // Send confirmation email
            sendApplicationConfirmationEmail(jobSeeker, job);
            
            redirectAttributes.addFlashAttribute("successMessage", "Application submitted successfully!");
            return "redirect:/jobseekers/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error submitting application: " + e.getMessage());
            return "redirect:/jobseekers/apply/" + jobId;
        }
    }

    private void sendApplicationConfirmationEmail(JobSeeker jobSeeker, Job job) {
        try {
            String subject = "Application Received - " + job.getTitle();
            StringBuilder body = new StringBuilder();
            body.append("Dear ").append(jobSeeker.getFirstName()).append(",\n\n");
            body.append("Thank you for submitting your application for the position of ")
                .append(job.getTitle())
                .append(" at ")
                .append(job.getCompany().getCompanyName())
                .append(".\n\n");
            body.append("If there is a good match between your qualifications and our requirements, we will contact you soon for the next steps.\n\n");
            body.append("Best regards,\n");
            body.append("Job Board Team");

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(jobSeeker.getEmail());
            message.setSubject(subject);
            message.setText(body.toString());
            emailService.send(message);
        } catch (Exception e) {
            // Log error but don't stop the application flow
            e.printStackTrace();
        }
    }
    
    @GetMapping("/jobseekers/applications")
    public String showApplications(Model model, Principal principal) {
        try {
            String email = principal.getName();
            JobSeeker jobSeeker = jobSeekerDao.findByEmail(email);
            List<JobApplication> jobApplications = jobSeekerDao.findApplicationsByJobSeeker(jobSeeker);
            
            // Initialize empty list if null
            if (jobApplications == null) {
                jobApplications = new ArrayList<>();
            }
            
            model.addAttribute("jobApplications", jobApplications);
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
            JobSeeker jobSeeker = jobSeekerDao.findByEmail(email);
            JobApplication jobApplication = jobSeekerDao.findApplicationById(applicationId);
            
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
    
    @GetMapping("/jobseekers/profile")
    public String showProfile(Model model, Principal principal) {
        String email = principal.getName();
        JobSeeker jobSeeker = jobSeekerDao.findByEmail(email);
        
        // Get statistics
        int totalApplications = jobSeekerDao.countTotalApplications(jobSeeker);
        List<JobApplication> pendingApplications = jobSeekerDao.findByJobSeekerAndStatus(jobSeeker, ApplicationStatus.PENDING);
        List<JobApplication> acceptedApplications = jobSeekerDao.findByJobSeekerAndStatus(jobSeeker, ApplicationStatus.ACCEPTED);
        
        model.addAttribute("jobSeeker", jobSeeker);
        model.addAttribute("totalApplications", totalApplications);
        model.addAttribute("pendingApplications", pendingApplications);
        model.addAttribute("acceptedApplications", acceptedApplications);
        
        return "jobseeker/profile";
    }

    @GetMapping("/jobseekers/profile/edit")
    public String showEditProfileForm(Model model, Principal principal) {
        String email = principal.getName();
        JobSeeker jobSeeker = jobSeekerDao.findByEmail(email);
        model.addAttribute("jobSeeker", jobSeeker);
        return "jobseeker/edit-profile";
    }

    @PostMapping("/jobseekers/profile/update")
    public String updateProfile(
            @ModelAttribute JobSeeker jobSeeker,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        try {
            String email = principal.getName();
            JobSeeker existingJobSeeker = jobSeekerDao.findByEmail(email);
            
            // Update only allowed fields
            existingJobSeeker.setFirstName(jobSeeker.getFirstName());
            existingJobSeeker.setLastName(jobSeeker.getLastName());
            // Don't update email as it's used for authentication
            
            jobSeekerDao.updateProfile(existingJobSeeker);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating profile: " + e.getMessage());
        }
        return "redirect:/jobseekers/profile";
    }

}
