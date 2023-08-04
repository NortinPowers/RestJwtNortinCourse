package by.nortin.restjwtproject.dto;

import static by.nortin.restjwtproject.utils.Constants.PASSWORD_NOT_BLANK;
import static by.nortin.restjwtproject.utils.Constants.PASSWORD_PATTERN;
import static by.nortin.restjwtproject.utils.Constants.USERNAME_NOT_BLANK;
import static by.nortin.restjwtproject.utils.Constants.USERNAME_PATTERN;

import by.nortin.restjwtproject.validator.PasswordMatching;
import by.nortin.restjwtproject.validator.UserExist;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

@Getter
@Setter
@PasswordMatching
@Valid
public class UserRegistrationDto {

    @NotBlank(message = USERNAME_NOT_BLANK)
    @Pattern(regexp = USERNAME_PATTERN, message = "Incorrect username")
    @UserExist
    private String username;
    @NotBlank(message = PASSWORD_NOT_BLANK)
    @Pattern(regexp = PASSWORD_PATTERN, message = "Incorrect password")
    private String password;
    @NotBlank(message = "Enter verify password")
    @Pattern(regexp = PASSWORD_PATTERN, message = "Incorrect  verify password")
    private String verifyPassword;
}
