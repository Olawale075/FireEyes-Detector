package sms.com.sms.dto;

import java.util.Set;

public class UserDTO {
    private String phoneNumbers;
    private String name;
    private String email;
    private Boolean isVerified;
    private String role;
    private String notificationPreference;
    private Set<String> gasDetectorMacs;


    public UserDTO() {}
    public UserDTO(String phoneNumbers, String name, String email, Boolean isVerified, String role, String notificationPreference, Set<String> gasDetectorMacs) {
        this.phoneNumbers = phoneNumbers;
        this.name = name;
        this.email = email;
        this.isVerified = isVerified;
        this.role = role;
        this.notificationPreference = notificationPreference;
        this.gasDetectorMacs = gasDetectorMacs;
    }

    public String getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(String phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getNotificationPreference() {
        return notificationPreference;
    }

    public void setNotificationPreference(String notificationPreference) {
        this.notificationPreference = notificationPreference;
    }

    public Set<String> getGasDetectorMacs() {
        return gasDetectorMacs;
    }

    public void setGasDetectorMacs(Set<String> gasDetectorMacs) {
        this.gasDetectorMacs = gasDetectorMacs;
    }
}
