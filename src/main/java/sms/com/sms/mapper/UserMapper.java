package sms.com.sms.mapper;

import org.mapstruct.*;
import sms.com.sms.dto.UserDTO;
import sms.com.sms.model.GasDetector;
import sms.com.sms.model.Users;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "gasDetectorMacs", expression = "java(mapGasDetectorMacs(user.getGasDetectors()))")
    UserDTO toDto(Users user);

    default Set<String> mapGasDetectorMacs(Set<GasDetector> gasDetectors) {
        return gasDetectors.stream()
                .map(GasDetector::getMacAddress)
                .collect(Collectors.toSet());
    }
}
