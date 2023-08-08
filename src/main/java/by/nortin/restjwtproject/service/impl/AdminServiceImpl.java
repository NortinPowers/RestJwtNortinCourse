package by.nortin.restjwtproject.service.impl;

import by.nortin.restjwtproject.dto.UserDto;
import by.nortin.restjwtproject.service.AdminService;
import by.nortin.restjwtproject.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserService userService;

    @Override
    public void setAdmin(Long id) {
        userService.setRoleAdmin(id);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }
}
