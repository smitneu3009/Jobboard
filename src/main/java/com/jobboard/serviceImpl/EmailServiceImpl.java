package com.jobboard.serviceImpl;

import com.jobboard.model.JobApplication;
import com.jobboard.model.ApplicationStatus;
import com.jobboard.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailServiceImpl implements EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Override
    public void sendStatusUpdateEmail(JobApplication application, ApplicationStatus oldStatus, ApplicationStatus newStatus) {
        String to = application.getJobSeeker().getEmail();
        String subject = "Application Status Update - " + application.getJob().getTitle();
        String body = buildEmailBody(application, newStatus);
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            logger.info("Status update email sent to: {}", to);
        } catch (Exception e) {
            logger.error("Failed to send status update email to: {}", to, e);
        }
    }
    
    private String buildEmailBody(JobApplication application, ApplicationStatus status) {
        StringBuilder body = new StringBuilder();
        body.append("Dear ").append(application.getJobSeeker().getFirstName()).append(",\n\n");
        body.append("We are writing to inform you about the latest status of your application for the position of ");
        body.append(application.getJob().getTitle()).append(" at ").append(application.getJob().getCompany().getCompanyName()).append(".\n\n");

        switch (status) {
            case REVIEWED:
                body.append("Good news! Your application is now under review by our hiring team. ");
                body.append("This means our team has recognized your potential and is carefully evaluating your application further.\n\n");
                body.append("We appreciate your patience as we work to find the best fit for this position. ");
                body.append("You will hear back from us soon with an update on the next steps.\n\n");
                break;

            case ACCEPTED:
                body.append("Congratulations! We are thrilled to inform you that your application has been accepted. ");
                body.append("Our team was highly impressed with your skills and qualifications, and we look forward to working with you.\n\n");
                body.append("A representative from our HR team will contact you shortly with details about the next steps.\n\n");
                body.append("We’re excited to welcome you aboard!\n\n");
                break;

            case REJECTED:
                body.append("We regret to inform you that your application for the position of ");
                body.append(application.getJob().getTitle()).append(" has not been selected at this time.\n\n");
                body.append("Please know that this decision was not an easy one, as we carefully reviewed every application. ");
                body.append("Unfortunately, we received a large number of highly qualified candidates for this role, and it was a challenging process to make a final decision.\n\n");
                body.append("We sincerely appreciate the time and effort you put into applying and encourage you to stay connected with us. ");
                body.append("There may be other roles in the future that better match your skills and experience.\n\n");
                body.append("We wish you the very best in your job search and future endeavors. Please don’t hesitate to reach out if you have any questions.\n\n");
                break;
        }

        body.append("Best regards,\n");
        body.append("The Job Board Team");
        return body.toString();
    }
    @Override
    public void send(SimpleMailMessage message) {
        try {
            message.setFrom(fromEmail);
            mailSender.send(message);
            logger.info("Email sent successfully to: {}", message.getTo()[0]);
        } catch (Exception e) {
            logger.error("Failed to send email to: {}", message.getTo()[0], e);
        }
    }

}