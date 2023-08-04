package by.nortin.restjwtproject.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class BaseResponse {

    private Integer status;
    private final LocalDate timestamp = LocalDate.now();
}
