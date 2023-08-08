package by.nortin.restjwtproject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class BaseDto {

    @Schema(description = "Identifier", example = "1")
    @Min(1)
    private Long id;
}
