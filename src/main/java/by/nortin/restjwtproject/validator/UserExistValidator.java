package by.nortin.restjwtproject.validator;

import by.nortin.restjwtproject.service.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserExistValidator implements ConstraintValidator<UserExist, String> {

    private final UserService userService;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {
        return !userService.isUserExist(username);
    }
}
