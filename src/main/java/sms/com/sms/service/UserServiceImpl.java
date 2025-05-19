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
import sms.com.sms.enums.UserRole;
import sms.com.sms.exception.ResourceNotFoundException;
import sms.com.sms.model.Users;
import sms.com.sms.repository.UsersRepository;

import java.util.*;

@Service

public class UserServiceImpl implements UserService {

    private final UsersRepository repository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private SmsService smsService;

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

    
    public String sendOtp(String phoneNumber) {
        String otp = otpService.generateOtp(phoneNumber);
        String message = "Your OTP is: " + otp;
        return smsService.sendSms(phoneNumber, message);
    }

    public boolean verifyOtpAndCreateUser(String phoneNumber, String inputOtp, String name) {
        boolean valid = otpService.verifyOtp(phoneNumber, inputOtp);
        if (valid) {
            Users user = new Users();
            user.setPhonenumber(phoneNumber);
            user.setName(name);
            repository.save(user);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public  ResponseEntity<String>verifyOtpAndCreateUser(Users user) {

         String phoneNumber = user.getPhonenumber();
         String inputOtp = user.getOtp();
        if ( phoneNumber == null ) {

            throw new IllegalArgumentException("Phone number is required");
        }
           if (!phoneNumber.startsWith("234")) {
            phoneNumber = "+234" + phoneNumber.replaceFirst("^0", "");
        }
        if ( inputOtp == null ) {

            throw new IllegalArgumentException("OTP is required");
        }
     boolean valid = otpService.verifyOtp(phoneNumber, inputOtp);
        if (valid) {
            if (isPhonenumberRegistered(user.getPhonenumber())) {
            return   ResponseEntity.ok("Phone number already registered.");
        }

        if (user.getRole() == null) {
            user.setRole(UserRole.ROLE_USER);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repository.save(user);
           return    ResponseEntity.ok("User registered successfully.");
        }
          return    ResponseEntity.ok("\"Fail to register user");
     
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

    @Override
    @Transactional
    public void deletes(String phonenumber) {
        Users details = repository.findById(phonenumber)
                .orElseThrow(() -> new ResourceNotFoundException("User with phone number not found"));
        repository.delete(details);
    }

    @Override
    @Transactional
    public Users updateProduct(String phonenumber, Users newDetails) {
        Users existingDetails = repository.findById(phonenumber)
                .orElseThrow(() -> new ResourceNotFoundException("User with phone number not found"));

        existingDetails.setName(newDetails.getName());
        existingDetails.setPhonenumber(newDetails.getPhonenumber());

        return repository.save(existingDetails);
    }

    public List<Users> getAllUsersWithGasDetectors() {
        return repository.findAll();
    }

    public Users getDetails(String phonenumber) {
        return repository.findById(phonenumber)
                .orElseThrow(
                        () -> new ResourceNotFoundException("User with phone number " + phonenumber + " not found"));
    
    }
}
