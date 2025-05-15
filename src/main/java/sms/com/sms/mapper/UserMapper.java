// package sms.com.sms.mapper;

// import org.mapstruct.Mapper;
// import org.mapstruct.Mapping;
// import sms.com.sms.dto.UserDTO;
// import sms.com.sms.model.Users;

// import java.util.stream.Collectors;

// @Mapper(componentModel = "spring")
// public interface UserMapper {

//     @Mapping(target = "gasDetectorMacs", expression = "java(user.getGasDetectors().stream().map(detector -> detector.getMacAddress()).collect(Collectors.toSet()))")
//     UserDTO toDto(Users user);  // ✅ this must match what your service calls
// }
