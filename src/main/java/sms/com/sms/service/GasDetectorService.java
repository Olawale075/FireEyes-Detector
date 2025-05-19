package sms.com.sms.service;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sms.com.sms.dto.DetectorDTO;
import sms.com.sms.mapper.DetectorMapper;
import sms.com.sms.model.GasDetector;
import sms.com.sms.model.Users;
import sms.com.sms.repository.GasDetectorRepository;
import sms.com.sms.repository.UsersRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GasDetectorService {

    private final GasDetectorRepository detectorRepository;
    private final UsersRepository usersRepository;
    private final DetectorMapper detectorMapper;

    public DetectorDTO create(DetectorDTO dto) {
        GasDetector detector = detectorMapper.toEntity(dto);

        // Link Users by phone numbers
        if (dto.getPhoneNumbers() != null) {
            Set<Users> users = dto.getPhoneNumbers().stream()
                    .map(usersRepository::findByPhonenumber)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());

            detector.setUsers(users);
        }

        return detectorMapper.toDto(detectorRepository.save(detector));
    }

    public List<DetectorDTO> findAll() {
        return detectorRepository.findAll().stream()
                .map(detectorMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<DetectorDTO> findByMac(String mac) {
        return detectorRepository.findById(mac)
                .map(detectorMapper::toDto);
    }

    public DetectorDTO update(String mac, DetectorDTO dto) {
        return detectorRepository.findById(mac).map(existing -> {
            existing.setLocation(dto.getLocation());
            existing.setStatus(dto.getStatus());
            existing.setTemperature(dto.getTemperature());
            existing.setHumidity(dto.getHumidity());

            if (dto.getPhoneNumbers() != null) {
                Set<Users> users = dto.getPhoneNumbers().stream()
                        .map(usersRepository::findByPhonenumber)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toSet());
                existing.setUsers(users);
            }

            return detectorMapper.toDto(detectorRepository.save(existing));
        }).orElseThrow(() -> new RuntimeException("Detector not found"));
    }

    public void delete(String mac) {
        detectorRepository.deleteById(mac);
    }
     public String assignDetectorToUser(String phoneNumber, String macAddress) {
        // logger.info("Assigning detector [{}] to user [{}]", macAddress, phoneNumber);

        Users user = usersRepository.findById(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Phone number not found"));

        GasDetector gasDetector = detectorRepository.findById(macAddress)
                .orElseThrow(() -> new RuntimeException("Gas detector not found"));

        if (user.getGasDetectors().contains(gasDetector)) {
            // throw new RuntimeException("Gas detector already assigned to this user");
            return "Gas detector already assigned to this user";
        }

        user.addGasDetector(gasDetector);
        Users details =usersRepository.save(user);
        return"Successfully Link";
    }
     public Page<DetectorDTO> getAllPaged(Pageable pageable) {
        Page<GasDetector> detectors = detectorRepository.findAll(pageable);
        return detectors.map(detectorMapper::toDto);
    }
}
