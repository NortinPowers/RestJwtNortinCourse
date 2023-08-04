package by.nortin.restjwtproject.validator;

import by.nortin.restjwtproject.dto.UserRegistrationDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchingValidator implements ConstraintValidator<PasswordMatching, UserRegistrationDto> {

    @Override
    public boolean isValid(UserRegistrationDto userRegistrationDto, ConstraintValidatorContext constraintValidatorContext) {
        return userRegistrationDto.getPassword().equals(userRegistrationDto.getVerifyPassword());
    }
}
