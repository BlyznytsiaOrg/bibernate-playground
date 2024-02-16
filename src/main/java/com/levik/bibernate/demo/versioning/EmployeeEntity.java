package com.levik.bibernate.demo.versioning;

import io.github.blyznytsiaorg.bibernate.annotation.Entity;
import io.github.blyznytsiaorg.bibernate.annotation.Id;
import io.github.blyznytsiaorg.bibernate.annotation.Table;
import io.github.blyznytsiaorg.bibernate.annotation.Version;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
@Table(name = "employees")
public class EmployeeEntity {
    @Id
    private Long id;

    private String firstName;

    private String lastName;

    @Version
    private Integer version;
}
