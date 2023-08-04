package by.nortin.restjwtproject.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class UserDto extends BaseDto{

    private String username;
    private String role;
}
