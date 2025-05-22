package sms.com.sms.dto;

import java.util.Set;

public class UserDTO {
    private String phoneNumbers;
    private String name;
    private String email;
   private String notificationPreference;
    private Set<String> macAddress;

    public UserDTO() {}

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


    public String getNotificationPreference() {
        return notificationPreference;
    }

    public void setNotificationPreference(String notificationPreference) {
        this.notificationPreference = notificationPreference;
    }

    public Set<String> getmacAddress() {
        return macAddress;
    }

    public void setmacAddress(Set<String> macAddress) {
        this.macAddress = macAddress;
    }
}
