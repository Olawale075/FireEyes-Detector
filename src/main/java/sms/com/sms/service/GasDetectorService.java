package sms.com.sms.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// import sms.com.sms.dto.DetectorDTO;
// // import sms.com.sms.dto.UserDTO;
// import sms.com.sms.mapper.DetectorMapper;
import sms.com.sms.model.GasDetector;
import sms.com.sms.model.Users;
import sms.com.sms.repository.GasDetectorRepository;
import sms.com.sms.repository.UsersRepository;

@Service
@Transactional // Ensures atomic operations during assignment
public class GasDetectorService {

    private static final Logger logger = LoggerFactory.getLogger(GasDetectorService.class);

    @Autowired
    private GasDetectorRepository gasDetectorRepository;

    @Autowired
    private UsersRepository usersRepository;
    
    public String assignDetectorToUser(String phoneNumber, String macAddress) {
        // logger.info("Assigning detector [{}] to user [{}]", macAddress, phoneNumber);

        Users user = usersRepository.findById(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Phone number not found"));

        GasDetector gasDetector = gasDetectorRepository.findById(macAddress)
                .orElseThrow(() -> new RuntimeException("Gas detector not found"));

        if (user.getGasDetectors().contains(gasDetector)) {
            // throw new RuntimeException("Gas detector already assigned to this user");
            return "Gas detector already assigned to this user";
        }

        user.addGasDetector(gasDetector);
        Users details =usersRepository.save(user);
        return"Successfully Link";
    }

    /**
     * Get a gas detector by its MAC address.
     */
    public ResponseEntity<GasDetector> getDetector(String macAddress) {
       try {
        ResponseEntity<GasDetector> detector = gasDetectorRepository.findByMacAddress(macAddress);

        if (detector == null) {
            logger.warn("Gas detector with MAC address [{}] not found", macAddress);
            throw new RuntimeException("MAC address not found");
        }

        return detector;
       } catch (Exception e) {
        throw new RuntimeException("MAC address not found"+ e.getMessage());
       } 
    }

    /**
     * Register a new gas detector.
     */
    public ResponseEntity<String> registerDetector(GasDetector gasDetector) {
        try {
            gasDetectorRepository.save(gasDetector);
            logger.info("Registered gas detector with MAC address [{}]", gasDetector.getMacAddress());
            return ResponseEntity.ok("Smoke Detector Registered.");
        } catch (Exception e) {
            logger.error("Error registering detector: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong: " + e.getMessage());
        }
    }

    /**
     * Get all gas detectors (for authenticated admins).
     */
 
     public List<GasDetector> getAllGasDetectors() {
        List<GasDetector> gasDetectors = gasDetectorRepository.findAll();
        return gasDetectors; // ✅ Collect the results correctly
    }
    

    }

