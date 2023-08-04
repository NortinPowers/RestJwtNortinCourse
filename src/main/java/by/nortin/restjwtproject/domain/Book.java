package by.nortin.restjwtproject.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Table(name = "books")
@Entity
public class Book extends BaseDomain {

    private String author;
    private String title;
}
