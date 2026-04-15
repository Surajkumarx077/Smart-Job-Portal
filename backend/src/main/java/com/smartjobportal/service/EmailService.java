package com.smartjobportal.service;

import java.time.Instant;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Value("${app.email.enabled:true}")
    private boolean emailEnabled;

    public void sendApplicationNotificationToRecruiter(String recruiterEmail, String jobTitle, String applicantName) {
        String subject = "New application for: " + jobTitle;
        String body = String.format(
                "Hello,\n\n%s has applied for the position \"%s\".\n\nPlease log in to view the application and candidate details.",
                applicantName, jobTitle
        );
        sendEmail(recruiterEmail, subject, body);
    }

    public void sendApplicationConfirmationToApplicant(String applicantEmail, String jobTitle, String company) {
        String subject = "Application received: " + jobTitle;
        String body = String.format(
                "Hello,\n\nThank you for applying for \"%s\" at %s. Your application has been received and is under review.\n\nBest regards,\nSmart Job Portal",
                jobTitle, company != null ? company : "the company"
        );
        sendEmail(applicantEmail, subject, body);
    }

    public void sendInterviewScheduleToApplicant(String applicantEmail,
                                                 String jobTitle,
                                                 Instant interviewAt,
                                                 String meetingLink) {
        String subject = "Interview scheduled: " + jobTitle;
        String body = String.format(
                "Hello,\n\nCongratulations. You have been shortlisted for \"%s\".\n\nInterview time (UTC): %s\nOnline meeting link: %s\n\nPlease join on time.\n\nBest regards,\nSmart Job Portal",
                jobTitle,
                interviewAt,
                meetingLink
        );
        sendEmail(applicantEmail, subject, body);
    }

    private void sendEmail(String to, String subject, String body) {
        if (mailSender == null || !emailEnabled || fromEmail == null || fromEmail.isEmpty()) {
            log.info("[Email disabled] Would send to {}: {} - {}", to, subject, body.length() > 50 ? body.substring(0, 50) + "..." : body);
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(Objects.requireNonNull(fromEmail, "fromEmail must not be null"));
            helper.setTo(Objects.requireNonNull(to, "to must not be null"));
            helper.setSubject(Objects.requireNonNull(subject, "subject must not be null"));
            helper.setText(Objects.requireNonNull(body, "body must not be null"), false);
            mailSender.send(message);
            log.info("Email sent to {}: {}", to, subject);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
