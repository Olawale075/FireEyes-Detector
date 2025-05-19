package sms.com.sms.dto;

import java.util.Set;

public class DetectorDTO {
    private String macAddress;
    private String location;
    private Boolean status;
    private Double temperature =0.0;
    private Double humidity =0.0;
    private Set<String> phoneNumbers;

    public DetectorDTO() {}

    public DetectorDTO(String macAddress, String location, Boolean status, Double temperature, Double humidity, Set<String> phoneNumbers) {
        this.macAddress = macAddress;
        this.location = location;
        this.status = status;
        this.temperature = temperature;
        this.humidity = humidity;
        this.phoneNumbers = phoneNumbers;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Set<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(Set<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
}
