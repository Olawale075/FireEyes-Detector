package sms.com.sms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sms.com.sms.dto.UserDTO;
import sms.com.sms.enums.NotificationPreference;
import sms.com.sms.enums.UserRole;
import sms.com.sms.exception.ResourceNotFoundException;
import sms.com.sms.mapper.UserMapper;
import sms.com.sms.model.Users;
import sms.com.sms.repository.UsersRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.*;

@Service

public class UserServiceImpl implements UserService {

    private final UsersRepository repository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private SmsService smsService;
   @Autowired
    private UserMapper userMapper;
    @Autowired
    private OTPService otpService;

    private final Map<String, Users> tempUserStorage = new HashMap<>();

    public UserServiceImpl(UsersRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean isPhonenumberRegistered(String phonenumber) {
        return repository.existsById(phonenumber);
    }

    @Override
    public void saveTempUser(Users user) {
        tempUserStorage.put(user.getPhonenumber(), user);
    }

    @Override
    public Users findTempUser(String phonenumber) {
        return tempUserStorage.get(phonenumber);
    }

    public String sendOtp(String to) {
        boolean exists = isPhonenumberRegistered(to);
        if (exists) {

            throw new IllegalArgumentException("Phone number as been Used");
        }
        if (to == null) {

            throw new IllegalArgumentException("Phone number is required");
        }
        if (!to.startsWith("234")) {
            to = "234" + to.replaceFirst("^0", "");
        }
        String otp = otpService.generateOtp(to);
        String message = "Dear User your Verification Pin is " + otp
                + " Valid for 5 minutes, one-time use only.(FireEyes)";
        return smsService.sendSms(to, message);
    }

    @Override
    @Transactional
    public ResponseEntity<String> verifyOtpAndCreateUser(Users user) {

        String phoneNumber = user.getPhonenumber();
        String inputOtp = user.getOtp();
        if (phoneNumber == null) {

            throw new IllegalArgumentException("Phone number is required");
        }
        if (!phoneNumber.startsWith("234")) {
            phoneNumber = "234" + phoneNumber.replaceFirst("^0", "");
        }
        if (inputOtp == null) {

            throw new IllegalArgumentException("OTP is required");
        }
        if (isPhonenumberRegistered(user.getPhonenumber())) {
            return ResponseEntity.ok("Phone number already registered.");
        }

        if (user.getRole() == null) {
            user.setRole(UserRole.ROLE_USER);
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            System.out.println(user);
            return ResponseEntity.badRequest().body("Email is required.");
        }
        boolean valid = otpService.verifyOtp(phoneNumber, inputOtp);
        if (valid) {

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            repository.save(user);
            return ResponseEntity.ok("User registered successfully.");
        }
       else  return ResponseEntity.ok("\"Fail to register user");

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = repository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String role = user.getRole().name().startsWith("ROLE_")
                ? user.getRole().name()
                : "ROLE_" + user.getRole().name();

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getPhonenumber())
                .password(user.getPassword())
                .authorities(new SimpleGrantedAuthority(role))
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------
    
public Page<UserDTO> getAllUsers(Pageable pageable) {
    return repository.findAll(pageable)
            .map(userMapper::toDto);
}

    public Optional<UserDTO> getUserByPhone(String phone) {
        return repository.findById(phone)
                .map(userMapper::toDto);
    }

  
    public Optional<UserDTO> updateUser(String phone, UserDTO dto) {
        return repository.findById(phone).map(existing -> {
            existing.setName(dto.getName());
            existing.setEmail(dto.getEmail());
            if (dto.getNotificationPreference() != null) {
                existing.setNotificationPreference(NotificationPreference.valueOf(dto.getNotificationPreference()));
            }
            return userMapper.toDto(repository.save(existing));
        });
    }

    public boolean deleteUser(String phone) {
        if (repository.existsById(phone)) {
            repository.deleteById(phone);
            return true;
        }
        return false;
    }
}
