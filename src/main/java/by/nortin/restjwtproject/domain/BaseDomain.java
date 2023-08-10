package by.nortin.restjwtproject.domain;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
@EqualsAndHashCode
public class BaseDomain implements Serializable {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
}
