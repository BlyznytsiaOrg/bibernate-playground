package com.levik.bibernate.demo;

import com.levik.bibernate.demo.flyway.Person;
import io.github.blyznytsiaorg.bibernate.Persistent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FlywayMigrationSupportApplication {
    public static final String ENTITY_PACKAGE = "com.levik.bibernate.demo.flyway";
    public static final String CONFIG_FILE_NAME = "bibernate-flyway.properties";
    @SneakyThrows
    public static void main(String[] args) {
        log.info("Bibernate Demo Application...");
        Persistent persistent = Persistent.withExternalConfiguration(ENTITY_PACKAGE, CONFIG_FILE_NAME);

        try (var bibernateEntityManager = persistent.createBibernateEntityManager()) {
            var bibernateSessionFactory = bibernateEntityManager.getBibernateSessionFactory();
            try (var session = bibernateSessionFactory.openSession()) {
                var person = new Person();
                person.setFirstName("Gavin");
                person.setLastName("King");

                session.save(Person.class, person);

                Person savedPerson = session.findById(Person.class, 1L).orElseThrow();
                log.info("Person {} ",savedPerson);
            }
        }
    }
}
