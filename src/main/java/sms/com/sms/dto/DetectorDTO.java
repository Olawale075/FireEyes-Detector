package sms.com.sms.dto;

import lombok.Data;
import java.util.Set;

@Data
public class DetectorDTO {
    private String macAddress;
    private String location;
    private Boolean status;
    private Set<String> getPhonenumber;
}
