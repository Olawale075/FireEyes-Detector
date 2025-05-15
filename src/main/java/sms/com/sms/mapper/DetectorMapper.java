// package sms.com.sms.mapper;

// import org.mapstruct.Mapper;
// import org.mapstruct.Mapping;
// import sms.com.sms.dto.DetectorDTO;
// import sms.com.sms.model.GasDetector;

// import java.util.stream.Collectors;

// @Mapper(componentModel = "spring")
// public interface DetectorMapper {
//     @Mapping(
//         target = "phoneNumbers",
//         expression = "java(detector.getUsers().stream().map(user -> user.getPhonenumber()).collect(Collectors.toSet()))"
//     )
//     DetectorDTO toDetectorDTO(GasDetector detector);
// }
