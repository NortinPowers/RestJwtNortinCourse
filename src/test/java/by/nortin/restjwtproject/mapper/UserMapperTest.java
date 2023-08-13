package by.nortin.restjwtproject.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import by.nortin.restjwtproject.domain.Role;
import by.nortin.restjwtproject.domain.User;
import by.nortin.restjwtproject.dto.UserDto;
import by.nortin.restjwtproject.dto.UserRegistrationDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    private final User user;
    private final UserDto userDto;
    private final UserRegistrationDto userRegistrationDto;

    {
        Long id = 1L;
        String username = "user";
        String password = "password";
        user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);
        String roleName = "ROLE_ADMIN";
        Role role = new Role();
        role.setId(id);
        role.setName(roleName);
        user.setRole(role);
        userDto = new UserDto();
        userDto.setId(id);
        userDto.setRole(roleName);
        userDto.setUsername(username);
        userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setUsername(username);
        userRegistrationDto.setPassword(password);
        userRegistrationDto.setVerifyPassword(password);
    }

    @Test
    void convertToDomain() {
        assertEquals(user.getUsername(), userMapper.convertToDomain(userRegistrationDto).getUsername());
        assertEquals(user.getPassword(), userMapper.convertToDomain(userRegistrationDto).getPassword());
    }

    @Test
    void convertToDto() {
        assertEquals(userDto, userMapper.convertToDto(user));
    }
}
