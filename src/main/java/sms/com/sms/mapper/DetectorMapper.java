package sms.com.sms.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sms.com.sms.dto.DetectorDTO;
import sms.com.sms.model.GasDetector;
import sms.com.sms.model.Users;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DetectorMapper {

    @Mapping(target = "getPhonenumber", expression = "java(mapPhoneNumbers(detector.getUsers()))")
    DetectorDTO toDto(GasDetector detector);

    // Default method to map Set<Users> to Set<String> of phone numbers
    default Set<String> mapPhoneNumbers(Set<Users> users) {
        if (users == null) return null;
        return users.stream()
        .map(user -> user.getPhonenumber())


                .collect(Collectors.toSet());
    }
}
