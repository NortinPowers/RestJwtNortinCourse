package by.nortin.restjwtproject.service.impl;

import static by.nortin.restjwtproject.utils.Constants.ENTITY_NOT_FOUND_EXCEPTION_MESSAGE;
import static by.nortin.restjwtproject.utils.Constants.ROLE_ADMIN;
import static by.nortin.restjwtproject.utils.Constants.ROLE_USER;
import static by.nortin.restjwtproject.utils.Constants.USERNAME_NOT_FOUND_EXCEPTION_MESSAGE;
import static by.nortin.restjwtproject.utils.ResponseUtils.DATA_SOURCE_LOOKUP_FAILURE_EXCEPTION_MESSAGE;

import by.nortin.restjwtproject.domain.Role;
import by.nortin.restjwtproject.domain.User;
import by.nortin.restjwtproject.dto.UserDto;
import by.nortin.restjwtproject.dto.UserRegistrationDto;
import by.nortin.restjwtproject.mapper.UserMapper;
import by.nortin.restjwtproject.repository.RoleRepository;
import by.nortin.restjwtproject.repository.UserRepository;
import by.nortin.restjwtproject.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(USERNAME_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    @Override
    public void save(UserRegistrationDto userRegistrationDto) {
        userRegistrationDto.setPassword(passwordEncoder.encode(userRegistrationDto.getPassword()));
        User user = userMapper.convertToDomain(userRegistrationDto);
        Optional<Role> optionalRole = roleRepository.findByName(ROLE_USER);
        if (optionalRole.isPresent()) {
            user.setRole(optionalRole.get());
            userRepository.save(user);
        } else {
            throw new DataSourceLookupFailureException(DATA_SOURCE_LOOKUP_FAILURE_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public boolean isUserExist(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Override
    public void setRoleAdmin(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            Optional<Role> optionalRole = roleRepository.findByName(ROLE_ADMIN);
            if (optionalRole.isPresent()) {
                User user = userOptional.get();
                Role role = optionalRole.get();
                user.setRole(role);
                userRepository.save(user);
            } else {
                throw new DataSourceLookupFailureException(DATA_SOURCE_LOOKUP_FAILURE_EXCEPTION_MESSAGE);
            }
        } else {
            throw new EntityNotFoundException(ENTITY_NOT_FOUND_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::convertToDto)
                .toList();
    }
}
