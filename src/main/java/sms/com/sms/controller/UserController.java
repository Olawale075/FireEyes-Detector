package sms.com.sms.controller;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
import sms.com.sms.dto.UserDTO;
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
 public ResponseEntity<Page<UserDTO>> getAllUsers( @PageableDefault(size = 10, sort = "createDateTime", direction = Sort.Direction.DESC) Pageable pageable) {

    return ResponseEntity.ok(service.getAllUsers(pageable));
     
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
      @Operation(summary = "verifyOtpAndCreateUser")
    @PostMapping("/verifyOtpAndCreateUser")
      public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO dto) {
        return ResponseEntity.ok(service.createUser(dto));
    }

    /** Get user details by phone number */
      @Operation(summary = "Get the User with the Phone Number")
    @GetMapping("/{phonenumber}")

   public ResponseEntity<UserDTO> getUser(@PathVariable String phonenumber) {
        Optional<UserDTO> user = service.getUserByPhone(phonenumber);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    /** Update user details */
      @Operation(summary = "Update the user using the PhoneNumbar ")
   
 @PutMapping("/{phone}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable String phonenumber, @RequestBody UserDTO dto) {
        Optional<UserDTO> updated = service.updateUser(phonenumber, dto);
        return updated.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /** Delete user by phone number */
      @Operation(summary = "Delete the the User using the phonenumber ")
    @DeleteMapping("admin/{phonenumber}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
   public ResponseEntity<Void> deleteUser(@PathVariable String phone) {
        if (service.deleteUser(phone)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
       @Operation(summary = "Sending OTP to the New user ")
   @PostMapping("sendOtp/{to}")
    public String sendOtp(@PathVariable String to) {
        return service.sendOtp(to);
    }

    // @GetMapping("/{phone}")
    // public Users getUserWithDetectors(@PathVariable String phone) {
    //     Users user = usersRepository.findById(phone)
    //             .orElseThrow(() -> new RuntimeException("User not found"));
    //     return user;
    // }
}
