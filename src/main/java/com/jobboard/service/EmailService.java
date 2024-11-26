package com.jobboard.service;

import com.jobboard.model.ApplicationStatus;
import com.jobboard.model.JobApplication;
import org.springframework.mail.SimpleMailMessage;

public interface EmailService {
    void sendStatusUpdateEmail(JobApplication application, ApplicationStatus oldStatus, ApplicationStatus newStatus);
    void send(SimpleMailMessage message);
}