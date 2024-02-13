package com.levik.bibernate.demo.ddl;

import io.github.blyznytsiaorg.bibernate.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToMany
    @JoinTable(name = "books_authors", joinColumn = @JoinColumn(name = "book_id"),
            inverseJoinColumn = @JoinColumn(name = "author_id"),
            foreignKey = @ForeignKey(name = "FK_book_book_authors"),
            inverseForeignKey = @ForeignKey(name = "FK_authors_book_authors"))
    List<Author> authors = new ArrayList<>();
}
