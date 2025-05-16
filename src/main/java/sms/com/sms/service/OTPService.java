package sms.com.sms.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OTPService {
    private final Map<String, String> otpStorage = new HashMap<>();

    public void storeOtp(String phonenumber, String otp) {
        otpStorage.put(phonenumber, otp);
    }

    public boolean validateOtp(String phonenumber, Object otp) {
        return otp.equals(otpStorage.get(phonenumber));
    }
}
