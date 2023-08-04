package by.nortin.restjwtproject.service;

import by.nortin.restjwtproject.domain.User;
import by.nortin.restjwtproject.dto.UserDto;
import by.nortin.restjwtproject.dto.UserRegistrationDto;
import java.util.List;

public interface UserService {

    User getUserByUsername(String username);

    void save(UserRegistrationDto userRegistrationDto);

    boolean isUserExist(String username);

    void setRoleAdmin(Long id);

    List<UserDto> getAllUsers();
}
