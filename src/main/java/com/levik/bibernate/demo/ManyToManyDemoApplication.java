package com.levik.bibernate.demo;

import com.levik.bibernate.demo.relations.Course;
import com.levik.bibernate.demo.relations.Person;
import io.github.blyznytsiaorg.bibernate.Persistent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class ManyToManyDemoApplication {

    public static final String ENTITY_PACKAGE = "com.levik.bibernate.demo.relations";

    @SneakyThrows
    public static void main(String[] args) {
        log.info("Bibernate Demo Application...");
        Persistent persistent = Persistent.withDefaultConfiguration(ENTITY_PACKAGE);

        try (var bibernateEntityManager = persistent.createBibernateEntityManager()) {
            var bibernateSessionFactory = bibernateEntityManager.getBibernateSessionFactory();
            try (var session = bibernateSessionFactory.openSession()) {
                var course = new Course();
                course.setName("Course_name1");

                Course savedCourse = session.save(Course.class, course);

                var person = new Person();
                person.setFirstName("Yevgen");
                person.getCourses().add(savedCourse);

                Person savedPerson = session.save(Person.class, person);

                List<Course> courses = savedPerson.getCourses();
                log.info("Person {} courses {}", person, Arrays.toString(courses.toArray()));
            }
        }
    }
}
