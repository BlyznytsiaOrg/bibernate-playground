package com.levik.bibernate.demo.relations;

import io.github.blyznytsiaorg.bibernate.annotation.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString(exclude = "courses")
@Entity
@Table(name = "authors")
public class Author {
    
    @Id
    @GeneratedValue
    private Long id;
    
    private String name;
    
    @OneToMany(mappedBy = "author")
    private List<Course> courses;
    
}
