package by.nortin.restjwtproject.dto;

import static by.nortin.restjwtproject.utils.Constants.PASSWORD_NOT_BLANK;
import static by.nortin.restjwtproject.utils.Constants.USERNAME_NOT_BLANK;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

@Getter
@Setter
public class JwtRequest {

    @NotBlank(message = USERNAME_NOT_BLANK)
    private String username;
    @NotBlank(message = PASSWORD_NOT_BLANK)
    private String password;
}
