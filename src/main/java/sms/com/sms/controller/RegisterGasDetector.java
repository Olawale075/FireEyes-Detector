package sms.com.sms.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import lombok.RequiredArgsConstructor;
import sms.com.sms.dto.DetectorDTO;
import sms.com.sms.model.GasDetector;
import sms.com.sms.model.Users;
import sms.com.sms.service.GasDetectorService;

@RestController
@RequestMapping("/gas-detectors")
@CrossOrigin("*")
@RequiredArgsConstructor

@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('ROLE_ADMIN')") // Applies to all methods unless overridden
public class RegisterGasDetector {

    @Autowired
    private final GasDetectorService gasDetectorService;

    @Operation(summary = "Assign detector to user")
    @PostMapping("/admin/assign")
    public ResponseEntity<String> assignDetector(
            @RequestParam String phonenumber,
            @RequestParam String macAddress) {
    gasDetectorService.assignDetectorToUser(phonenumber, macAddress);
        return ResponseEntity.ok("Succesfully Link");
    }

    @Operation(summary = "Get gas detector by MAC address")
    @GetMapping("/admin/getDetector")
    public  Optional<GasDetector> getDetector(@RequestParam String macAddress) {
        return gasDetectorService.getDetector(macAddress);
    }

    @Operation(summary = "Register a new gas detector")
    @PostMapping("/admin/register")
    public ResponseEntity<String> registerDetector(@RequestBody GasDetector gasDetector) {
        try {
            gasDetectorService.registerDetector(gasDetector);
            return ResponseEntity.ok("Smoke Detector Registered.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Smoke Detector registration failed: " + e.getMessage());
        }
    }

    @Operation(summary = "Get all gas detectors", description = "Returns all registered gas detectors. Requires ROLE_ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "403", description = "Access Denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/all")
    public List<DetectorDTO> getAllGasDetectors() {
        return gasDetectorService.getAllGasDetectors();
    }
    
    }

