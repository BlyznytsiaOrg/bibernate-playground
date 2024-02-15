package com.levik.bibernate.demo.ddl;

import io.github.blyznytsiaorg.bibernate.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "author_profiles")
public class AuthorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "text")
    private String aboutMe;

    @OneToOne
    @JoinColumn(name = "author_id")
    private Author author;

    @OneToMany(mappedBy = "authorProfile")
    private List<Phone> phones = new ArrayList<>();
}
