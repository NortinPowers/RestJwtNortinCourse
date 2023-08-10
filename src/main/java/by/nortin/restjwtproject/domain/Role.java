package by.nortin.restjwtproject.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Table(name = "roles")
@Entity
public class Role extends BaseDomain implements Serializable {

    private String name;
    @OneToMany
    private List<User> users;
}
