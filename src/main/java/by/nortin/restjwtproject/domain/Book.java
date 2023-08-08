package by.nortin.restjwtproject.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Table(name = "books")
@Entity
public class Book extends BaseDomain {

    private String author;
    private String title;
}
