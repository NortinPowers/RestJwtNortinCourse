package by.nortin.restjwtproject.dto;

import static by.nortin.restjwtproject.utils.Constants.AUTHOR_PATTERN;

import by.nortin.restjwtproject.utils.Constants;
import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class BookDto extends BaseDto {

    @NotBlank(message = "Enter author")
    @Pattern(regexp = AUTHOR_PATTERN, message = "Incorrect author`s name")
    private String author;
    @NotBlank(message = "Enter title")
    private String title;
}
