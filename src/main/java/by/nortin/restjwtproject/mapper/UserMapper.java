package by.nortin.restjwtproject.mapper;

import by.nortin.restjwtproject.domain.User;
import by.nortin.restjwtproject.dto.UserDto;
import by.nortin.restjwtproject.dto.UserRegistrationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = BaseMapper.class)
public interface UserMapper {

    User convertToDomain(UserRegistrationDto userRegistrationDto);

    @Mapping(target = "role", source = "user.role.name")
    UserDto convertToDto(User user);
}
