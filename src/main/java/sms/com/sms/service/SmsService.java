package sms.com.sms.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import sms.com.sms.dto.SmsRequest;

@Service
public class SmsService {

    private final String TERMII_SMS_URL = "https://api.ng.termii.com/";

    public String sendSms(String to, String message) {
        RestTemplate restTemplate = new RestTemplate();

        SmsRequest smsRequest = new SmsRequest();
        smsRequest.setTo(to);
        smsRequest.setFrom("N-Alert"); // Must be approved by Termii
        smsRequest.setSms(message);
        smsRequest.setApi_key("TLbkDkdbzTvdwHKsyPAmTshoMKbFnqidbWRDpAxfpofaDiOGGBDpPCcwaHpuMk");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SmsRequest> entity = new HttpEntity<>(smsRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                TERMII_SMS_URL,
                HttpMethod.POST,
                entity,
                String.class
        );

        return response.getBody();
    }
}

