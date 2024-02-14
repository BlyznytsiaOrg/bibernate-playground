package com.levik.bibernate.demo.transaction;

import io.github.blyznytsiaorg.bibernate.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static io.github.blyznytsiaorg.bibernate.annotation.GenerationType.IDENTITY;

@Entity
@Table(name = "persons")
@ToString
@Setter
@Getter
public class Person {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id")
  private Long id;

  private String firstName;

  @Column(name = "last_name")
  private String lastName;
}
