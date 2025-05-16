package sms.com.sms.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

public class UserServiceImpl implements UserService{
    
    private final UsersRepository repository;
    private final PasswordEncoder passwordEncoder;

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

    @Override
    @Transactional
    public Users saveUser(Users user) {
        if (user.getPhonenumber() == null) {
            throw new IllegalArgumentException("Phone number is required");
        }
        return repository.save(user);
    }

    @Override
    @Transactional
    public String register(Users details) {
        if (isPhonenumberRegistered(details.getPhonenumber())) {
            return "Phone number already registered.";
        }

        if (details.getRole() == null) {
            details.setRole(UserRole.ROLE_USER);
        }

        details.setPassword(passwordEncoder.encode(details.getPassword()));
        repository.save(details);
        return "User registered successfully.";
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
                .orElseThrow(() -> new ResourceNotFoundException("User with phone number " + phonenumber + " not found"));
    }
}
