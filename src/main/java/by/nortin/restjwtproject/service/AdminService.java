package by.nortin.restjwtproject.service;

import by.nortin.restjwtproject.dto.UserDto;
import java.util.List;

public interface AdminService {

    void setAdmin(Long id);

    List<UserDto> getAllUsers();
}
