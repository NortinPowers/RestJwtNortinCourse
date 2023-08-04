package by.nortin.restjwtproject.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ExceptionResponse extends BaseResponse{

    private String message;
    private String type;

    public ExceptionResponse(HttpStatus status, String message, String type) {
        super(status.value());
        this.message = message;
        this.type = type;
    }
}
