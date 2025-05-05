package sms.com.sms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sms.com.sms.config.SecurityConfig;
import sms.com.sms.dto.UserDTO;
import sms.com.sms.enums.UserRole;
import sms.com.sms.exception.ResourceNotFoundException;
import sms.com.sms.mapper.UserMapper;
import sms.com.sms.model.Users;
import sms.com.sms.repository.UsersRepository;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersRepository repository;
    
    @Autowired
    private SecurityConfig securityConfig;
    @Autowired
    private UserMapper userMapper;

    private final Map<String, Users> tempUserStorage = new HashMap<>();

    public boolean isPhoneNumberRegistered(String phonenumber) {
        return repository.existsById(phonenumber); // ✅ Use existsById
    }

    public void saveTempUser(Users user) {
        tempUserStorage.put(user.getPhonenumber(), user);
    }

    public Users findTempUser(String phonenumber) {
        return tempUserStorage.get(phonenumber);
    }

    public Users saveUser(Users user) {
        return repository.save(user);
    }

    public String register(Users details) {
        if (isPhoneNumberRegistered(details.getPhonenumber())) {
            return "Phone number already registered.";
        }

        if (details.getRole() == null) {
            details.setRole(UserRole.ROLE_USER);
        }

        details.setPassword(securityConfig.passwordEncoder().encode(details.getPassword()));

        repository.save(details);
        return "User registered successfully.";
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        Users user = repository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String role = user.getRole().name().startsWith("ROLE_") ? user.getRole().name() : "ROLE_" + user.getRole().name();

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getPhonenumber())
                .password(user.getPassword())
                .authorities(new SimpleGrantedAuthority(role))
                .build();
    }

    public void deletes(String phonenumber) {
        Users details = repository.findById(phonenumber)
                .orElseThrow(() -> new ResourceNotFoundException("Number not found"));
        repository.delete(details);
    }

    public List<UserDTO> getAllUsersWithGasDetectors() {
        List<Users> users = repository.findAll();
        return users.stream()
                    .map(userMapper::toDto)
                    .toList();
    }
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return repository.findAll(pageable)
                         .map(userMapper::toDto);
    }
    
    
    public UserDTO getDetails(String phonenumber) {
        Users user = repository.findById(phonenumber)
                .orElseThrow(() -> new ResourceNotFoundException("User with phone number " + phonenumber + " not found"));
        return userMapper.toDto(user);
    }


    public Users updateProduct(String phonenumber, Users newDetails) {
        Users existingDetails = repository.findById(phonenumber)
                .orElseThrow(() -> new ResourceNotFoundException("Number not found"));

        existingDetails.setName(newDetails.getName());
        existingDetails.setPhonenumber(newDetails.getPhonenumber());

        return repository.save(existingDetails);
    }
    
}
