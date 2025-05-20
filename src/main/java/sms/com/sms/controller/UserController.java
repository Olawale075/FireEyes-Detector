package sms.com.sms.controller;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import sms.com.sms.config.JwtUtil;
import sms.com.sms.dto.AuthRequest;
import sms.com.sms.dto.AuthResponse;
//  import sms.com.sms.dto.UserDTO;
import sms.com.sms.model.Users;
import sms.com.sms.repository.UsersRepository;

import sms.com.sms.service.OTPService;
import sms.com.sms.service.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin("*")
@RequestMapping("/user")
public class UserController {

    private final UserServiceImpl service;
    private final OTPService otpService;
    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;


    public UserController(
            UserServiceImpl service,
            OTPService otpService,
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil) {
        this.service = service;
        this.otpService = otpService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    private final AtomicInteger validatedUsersCount = new AtomicInteger(0);
    private static final int MAX_VALIDATED_USERS = 20;

    /** Get all users */
    
    @Operation(summary = "Get all the details of the Users")
    @GetMapping("/admin/")
    public List<Users> getAllUsers() {
        return service.getAllUsersWithGasDetectors();
    }

    // @GetMapping("/admin/page")
    // public Page<UserDTO> getAllUsers(Pageable pageable) {
    // // return service.getAllUsers(pageable);

    /** Get logged-in user details */
    
    @Operation(summary = "Get all the details of the Users")
    @GetMapping("/admin/details")

    public ResponseEntity<Users> getUsers() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof Users) {
            return ResponseEntity.ok((Users) principal);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /** Register a new user */
    @Operation(summary = "Register a new User")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Users user) {
        if (user.getPhonenumber() == null || user.getPhonenumber().isEmpty()) {
            return ResponseEntity.badRequest().body("Phone number is required.");
        }
        else if (user.getEmail()==null || user.getEmail().isEmpty()){
            System.out.println(user);
             return ResponseEntity.badRequest().body("Email is required.");
        }
return service.verifyOtpAndCreateUser(user);
       
    }

    /** Authenticate user and return JWT token */
    @Operation(summary = "Log in with PhoneNumber and PassWord")
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            System.out.println("🔹 Attempting login for: " + request.getPhoneNumber());

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getPhoneNumber(), request.getPassword()));

            System.out.println("✅ Authentication successful!");

            UserDetails userDetails = service.loadUserByUsername(request.getPhoneNumber());
            System.out.println("🔹 Loaded User: " + userDetails.getUsername());

            String token = jwtUtil.generateToken(userDetails.getUsername());

            return ResponseEntity.ok(new AuthResponse(token));
        } catch (BadCredentialsException e) {
            System.out.println("❌ Invalid Credentials");
            return ResponseEntity.status(401).body("Invalid credentials" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); // Print full error stack trace
            return ResponseEntity.status(403).body("Forbidden" + e.getMessage());
        }
    }
@Operation(summary = "Checking the Token")
    @GetMapping("/test")
    public ResponseEntity<String> testToken(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            System.out.println("🔹 Received Token: " + token);
            return ResponseEntity.ok("Token received successfully");

        } catch (Exception e) {
            e.printStackTrace(); // Print full error stack trace
            return ResponseEntity.status(403).body("Forbidden" + e.getMessage());
        }

    }

    /** Send fire detection alert */
    @Operation(summary = "Sending SMS to all user not working yet")
    @PostMapping("/send-message-to-all-for-fireDetector")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> sendMessageToAllForFireDetector() {
        String message = "Dear Subscriber,\n\nA GAS hazard has been detected at Moremi Hall, Osun State Government Secretariat, Oke Pupa, Abere, Osogbo. \n"
                +
                "This is an emergency situation, and your immediate action is required.\n" +
                "Stay alert and take care.\n\nLocation: Moremi Hall, Osun State Government Secretariat, Oke Pupa, Abere, Osogbo\n"
                +
                "[Your Organization/FireEye]";
        return ResponseEntity.ok("null");
    }

    /** Send gas detection alert */
      @Operation(summary = "Sending SMS to all user not working yet")
    @PostMapping("/send-message-to-all-for-GasDetector")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> sendMessageToAllForGasDetector() {
        String message = "Dear Subscriber,\n\nA GAS hazard has been detected at Adegunwa Kitchen. \n" +
                "This is an emergency situation, and your immediate action is required.\n" +
                "Stay alert and take care.\n\nLocation: FOUNTAIN UNIVERSITY / Adegunwa Kitchen\n" +
                "[Your Organization/ROBOTIC GROUP]";
        return ResponseEntity.ok("SMS not integrate");
    }

    /** Validate OTP and register user */
      @Operation(summary = "Validate the OTP")
    @PostMapping("/validate-otp")
    public ResponseEntity<String> validateOtp(@RequestBody Users details) {
      
            service.verifyOtpAndCreateUser(details);
            return ResponseEntity.ok("User registered successfully.");
        
    }

    /** Get user details by phone number */
      @Operation(summary = "Get the User with the Phone Numbar")
    @GetMapping("/{phonenumber}")

    public ResponseEntity<Users> getReceiverByPhonenumber(@PathVariable String phonenumber) {
        Users details = service.getDetails(phonenumber);
        return ResponseEntity.ok(details);
    }

    /** Update user details */
      @Operation(summary = "Update the user using the PhoneNumbar ")
    @PutMapping("/admin/{phonenumber}/update")

    public ResponseEntity<Users> updateReceiverDetails(@PathVariable String phonenumber,
            @RequestBody Users newDetails) {
        Users updatedDetails = service.updateProduct(phonenumber, newDetails);
        return ResponseEntity.ok(updatedDetails);
    }

    /** Delete user by phone number */
      @Operation(summary = "Delete the the User using the phonenumber ")
    @DeleteMapping("admin/{phonenumber}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteReceiver(@PathVariable String phonenumber) {
        service.deletes(phonenumber);
        return ResponseEntity.ok("Receiver deleted successfully.");
    }
       @Operation(summary = "Sending OTP to the New user ")
   @PostMapping("sendOtp/{phonenumber}")
    public String sendOtp(@PathVariable String phonenumber) {
        return service.sendOtp(phonenumber);
    }

    // @GetMapping("/{phone}")
    // public Users getUserWithDetectors(@PathVariable String phone) {
    //     Users user = usersRepository.findById(phone)
    //             .orElseThrow(() -> new RuntimeException("User not found"));
    //     return user;
    // }
}
