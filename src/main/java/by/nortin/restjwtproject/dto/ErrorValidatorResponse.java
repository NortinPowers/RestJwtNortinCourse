package by.nortin.restjwtproject.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ErrorValidatorResponse extends BaseResponse {

    private List<String> errors;
    private String message;

    public ErrorValidatorResponse(HttpStatus status, List<String> errors, String message) {
        super(status.value());
        this.errors = errors;
        this.message = message;
    }
}
