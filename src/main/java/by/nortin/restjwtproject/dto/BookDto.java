package by.nortin.restjwtproject.dto;

import static by.nortin.restjwtproject.utils.Constants.AUTHOR_PATTERN;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Entity of Book")
public class BookDto extends BaseDto {

    @NotBlank(message = "Enter author")
    @Pattern(regexp = AUTHOR_PATTERN, message = "Incorrect author`s name")
    @Schema(description = "Author", example = "Stephen King")
    private String author;
    @NotBlank(message = "Enter title")
    @Schema(description = "Title", example = "The Dark Tower")
    private String title;
}
