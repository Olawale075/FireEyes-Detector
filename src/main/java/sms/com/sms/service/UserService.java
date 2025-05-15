package sms.com.sms.service;

import org.springframework.security.core.userdetails.UserDetailsService;

// import sms.com.sms.dto.UserDTO;
// import sms.com.sms.dto.UserDTO;
import sms.com.sms.model.Users;
import java.util.List;

public interface UserService extends UserDetailsService {
    boolean isPhoneNumbersRegistered(String phoneNumbers);
    void saveTempUser(Users user);
    Users findTempUser(String phoneNumbers);
    Users saveUser(Users user);
    String register(Users details);
    void deletes(String phoneNumbers);
    //List<Users> getAllDetails();
     Users getDetails(String phoneNumbers);
    Users updateProduct(String phoneNumbers, Users newDetails);
}
