package sms.com.sms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import sms.com.sms.enums.NotificationPreference;
import sms.com.sms.enums.UserRole;

import java.time.LocalDateTime;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "gasDetectors")
@ToString(exclude = "gasDetectors")
@Entity
@DynamicUpdate
@Table(name = "users")
public class Users implements UserDetails {

    @Id
    @Column(name = "phone_number", nullable = false, unique = true)
    @NotNull
    private String phonenumber;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;
    public boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }

    // Getter and Setter for notificationPreference
    public NotificationPreference getNotificationPreference() {
        return notificationPreference;
    }

    public void setNotificationPreference(NotificationPreference notificationPreference) {
        this.notificationPreference = notificationPreference;
    }
    @Column(nullable = false)
    private Boolean isVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private NotificationPreference notificationPreference;

    @Transient
    private String otp;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_gas_detector", joinColumns = @JoinColumn(name = "user_phonenumber", referencedColumnName = "phone_number"), inverseJoinColumns = @JoinColumn(name = "gas_detector_mac", referencedColumnName = "mac_address"))
    private Set<GasDetector> gasDetectors = new HashSet<>();

    public void addGasDetector(GasDetector gasDetector) {
        this.gasDetectors.add(gasDetector);
        gasDetector.getUsers().add(this);
    }

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createDateTime;

    public String getPhonenumber() {
        return phonenumber;
    }

    @UpdateTimestamp
    private LocalDateTime updateDateTime;

    // --- Manually added methods used in controller/service ---

    public String getOtp() {
        return otp;
    }
public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    void setOtp(String otp) {
        this.otp = otp;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public Set<GasDetector> getGasDetectors() {
        return gasDetectors;
    }

    // --- Spring Security UserDetails Implementation ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    @Override
    public String getUsername() {
        return phonenumber;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
