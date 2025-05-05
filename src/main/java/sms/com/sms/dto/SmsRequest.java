package sms.com.sms.dto;

import lombok.Data;

@Data
public class SmsRequest {
    private String to;
    private String from;
    private String sms;
    private String type = "plain"; // fixed
    private String channel = "generic"; // or "dnd"
    private String api_key;

    // Getters and setters
}
