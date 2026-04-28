package com.example.bloodbank.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ResendEmailService {

    @Value("${resend.api.key}")
    private String resendApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String RESEND_URL = "https://api.resend.com/emails";

    public void sendEmergencyBroadcast(String toEmail, String patientName, String bloodGroup, String hospitalName) {
        if (toEmail == null || toEmail.isEmpty()) {
            log.warn("Cannot send email: Patient {} has no email address.", patientName);
            return;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(resendApiKey);

            String htmlBody = String.format(
                    "<h2>🚨 URGENT: %s Blood Needed!</h2>" +
                    "<p>Dear %s,</p>" +
                    "<p><strong>%s</strong> is facing a critical emergency and is in urgent need of <strong>%s</strong> blood.</p>" +
                    "<p>Because you are perfectly healthy and have not donated recently, you are eligible to save a life today.</p>" +
                    "<p>Please visit the hospital immediately if you can help.</p>" +
                    "<br/><p>Thank you,<br/>The Blood Bank Network</p>",
                    bloodGroup, patientName, hospitalName, bloodGroup
            );

            Map<String, Object> body = new HashMap<>();
            // Note: Resend requires a verified domain to send FROM. If you don't have one, 'onboarding@resend.dev' works for testing if 'toEmail' is verified in your account.
            body.put("from", "Emergency <onboarding@resend.dev>");
            body.put("to", List.of(toEmail));
            body.put("subject", "URGENT: " + bloodGroup + " Blood Needed at " + hospitalName);
            body.put("html", htmlBody);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            
            restTemplate.postForObject(RESEND_URL, request, String.class);
            log.info("Emergency email successfully dispatched to {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
        }
    }
}
