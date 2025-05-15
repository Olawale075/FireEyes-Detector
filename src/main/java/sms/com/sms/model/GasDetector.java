package sms.com.sms.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "users")
@ToString(exclude = "users")
@Entity
@Table(name = "gas_detectors")
public class GasDetector {

    @Id
    @Column(name = "mac_address", nullable = false, unique = true)
    private String macAddress;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Boolean status;

    @Column(nullable = false)
    private Double temperature;

    @Column(nullable = false)
    private Double humidity;

    @ManyToMany(mappedBy = "gasDetectors", fetch = FetchType.EAGER)
    private Set<Users> users = new HashSet<>();

    // --- Manually added for bi-directional control ---
    public String getMacAddress() {
        return macAddress;
    }
    
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
    
    public Set<Users> getUsers() {
        return users;
    }

    public void setUsers(Set<Users> users) {
        this.users = users;
    }

    public void addUser(Users user) {
        this.users.add(user);
        user.getGasDetectors().add(this);
    }

    public void removeUser(Users user) {
        this.users.remove(user);
        user.getGasDetectors().remove(this);
    }
}
