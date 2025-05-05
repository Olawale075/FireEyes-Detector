package sms.com.sms.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EqualsAndHashCode(exclude = "users")
@ToString(exclude = "users")
@Entity
public class GasDetector {

    @Id
 
    private String macAddress;
    private String location;
    private Boolean status;

    @ManyToMany(mappedBy = "gasDetectors", fetch = FetchType.EAGER)
    private Set<Users> users = new HashSet<>();
    
    // other fields, getters/setters
}
