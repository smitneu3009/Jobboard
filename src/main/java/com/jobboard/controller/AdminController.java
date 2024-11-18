package com.jobboard.controller;

import com.jobboard.model.JobSeeker;
import com.jobboard.service.JobSeekerService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {
	@Autowired
    private JobSeekerService jobSeekerService;

    @GetMapping("/admin/login")
    public String showAdminLoginForm() {
        return "admin/login";
    }

    @GetMapping("/admin/dashboard")
    public String showAdminDashboard(Model model) {
        List<JobSeeker> jobSeekers = jobSeekerService.getAllJobSeekers();
        model.addAttribute("jobSeekers", jobSeekers);
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
            jobSeekerService.updateProfile(jobSeeker);
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
}