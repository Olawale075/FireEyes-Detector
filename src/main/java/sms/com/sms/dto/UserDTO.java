package sms.com.sms.dto;

import lombok.Data;
import sms.com.sms.enums.NotificationPreference;
import sms.com.sms.enums.UserRole;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserDTO {
    private String phonenumber;
    private String name;
    private String email;
    private Boolean isVerified;
    private UserRole role;
    private NotificationPreference notificationPreference;
    private LocalDateTime createDateTime;
    private LocalDateTime updateDateTime;
    private Set<String> gasDetectorMacs; // Just MAC addresses, not full objects
}
