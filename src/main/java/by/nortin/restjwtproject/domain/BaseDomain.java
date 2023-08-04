package by.nortin.restjwtproject.domain;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import static jakarta.persistence.GenerationType.IDENTITY;

@MappedSuperclass
@Getter
@Setter
@EqualsAndHashCode
public class BaseDomain {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
}
