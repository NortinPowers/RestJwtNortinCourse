package by.nortin.restjwtproject.controller;

import static by.nortin.restjwtproject.utils.Constants.USER;
import static by.nortin.restjwtproject.utils.ResponseUtils.CREATION_MESSAGE;
import static by.nortin.restjwtproject.utils.ResponseUtils.getSuccessResponse;

import by.nortin.restjwtproject.dto.BaseResponse;
import by.nortin.restjwtproject.dto.JwtRequest;
import by.nortin.restjwtproject.dto.JwtResponse;
import by.nortin.restjwtproject.dto.UserRegistrationDto;
import by.nortin.restjwtproject.service.AuthService;
import by.nortin.restjwtproject.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<JwtResponse> createAuthToken(@RequestBody @Valid JwtRequest request) throws BadCredentialsException {
        String token = authService.getToken(request);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping("/registration")
    public ResponseEntity<BaseResponse> createNewUser(@RequestBody @Valid UserRegistrationDto userRegistrationDto) {
        userService.save(userRegistrationDto);
        return ResponseEntity.ok(getSuccessResponse(CREATION_MESSAGE, USER));
    }
}
