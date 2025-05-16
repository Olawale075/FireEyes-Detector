package sms.com.sms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sms.com.sms.config.SecurityConfig;
// import sms.com.sms.dto.UserDTO;
import sms.com.sms.enums.UserRole;
import sms.com.sms.exception.ResourceNotFoundException;
// import sms.com.sms.mapper.UserMapper;
import sms.com.sms.model.Users;
import sms.com.sms.repository.UsersRepository;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersRepository repository;

    @Autowired
    private SecurityConfig securityConfig;

    // Temporary in-memory storage (for OTP or pre-verification)
    private final Map<String, Users> tempUserStorage = new HashMap<>();

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

        details.setPassword(securityConfig.passwordEncoder().encode(details.getPassword()));
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
         List<Users> users = repository.findAll();
     return users;
     }

    //  @Override
    //  public Page<UserDTO> getAllUsers(Pageable pageable) {
    //      return repository.findAll(pageable)
    //                       .map(userMapper::toDto);
    //  }


     public Users getDetails(String phonenumber) {
         Users UserD = repository.findById(phonenumber)
                 .orElseThrow(() -> new ResourceNotFoundException("User with phone number " + phonenumber + " not found"));
         return UserD;
     }
    
}
