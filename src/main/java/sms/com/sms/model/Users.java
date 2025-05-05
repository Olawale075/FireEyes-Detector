// User Entity (Users.java)
package sms.com.sms.model;

import jakarta.persistence.*;
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
@EqualsAndHashCode(exclude = "gasDetectors") // 🔥 prevents recursion
@ToString(exclude = "gasDetectors") // Optional: avoids infinite loop in logs
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@DynamicUpdate

@Getter
@Setter
@Table(name = "users")
public class Users implements UserDetails {
    @Id
    @Column(unique = true, nullable = false)
    private String phonenumber;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

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
    @JoinTable(
        name = "user_gas_detector",
        joinColumns = @JoinColumn(name = "user_phonenumber", referencedColumnName = "phonenumber"),
        inverseJoinColumns = @JoinColumn(name = "gas_detector_mac", referencedColumnName = "macAddress")
    )
    private Set<GasDetector> gasDetectors = new HashSet<>();

    public void addGasDetector(GasDetector gasDetector) {
        this.gasDetectors.add(gasDetector);
        gasDetector.getUsers().add(this);
    }

    @CreationTimestamp
    private LocalDateTime createDateTime;

    @UpdateTimestamp
    private LocalDateTime updateDateTime;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
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
}
