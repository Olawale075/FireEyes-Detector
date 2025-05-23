package sms.com.sms.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sms.com.sms.dto.SmsRequest;

@Service
public class SmsService {

    // This should be set in application.properties or environment
    @Value("${termii.api.key}")
    private String apiKey;

    private final String TERMII_SMS_URL = "https://api.ng.termii.com/api/sms/send";

    public String sendSms(String to, String message) {
        RestTemplate restTemplate = new RestTemplate();

        SmsRequest smsRequest = new SmsRequest();
        smsRequest.setTo(to);
        smsRequest.setFrom("FireEyes"); // Must be approved by Termii
        smsRequest.setSms(message);
        smsRequest.setType("plain"); // Usually required by Termii
        smsRequest.setChannel("generic"); // Usually required by Termii
        smsRequest.setApi_key(apiKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SmsRequest> entity = new HttpEntity<>(smsRequest, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    TERMII_SMS_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            return response.getBody();

        } catch (Exception e) {
            return "SMS sending failed: " + e.getMessage();
        }
    }
}
