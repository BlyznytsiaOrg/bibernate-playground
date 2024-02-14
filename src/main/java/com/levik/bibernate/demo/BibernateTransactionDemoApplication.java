package com.levik.bibernate.demo;

import com.levik.bibernate.demo.transaction.Person;
import io.github.blyznytsiaorg.bibernate.Persistent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class BibernateTransactionDemoApplication {

    public static final String ENTITY_PACKAGE = "com.levik.bibernate.demo.transaction";

    @SneakyThrows
    public static void main(String[] args) {
        log.info("Bibernate Demo Application...");
        Persistent persistent = Persistent.withDefaultConfiguration(ENTITY_PACKAGE);

        try (var bibernateEntityManager = persistent.createBibernateEntityManager()) {
            var bibernateSessionFactory = bibernateEntityManager.getBibernateSessionFactory();
            try (var bibernateSession = bibernateSessionFactory.openSession()) {
                bibernateSession.startTransaction();
                var person = new Person();
                person.setFirstName("John");
                person.setLastName("Smith");
                bibernateSession.save(Person.class, person);
                bibernateSession.commitTransaction();
            }

            try (var bibernateSession = bibernateSessionFactory.openSession()) {
                bibernateSession.startTransaction();
                var person = new Person();
                person.setFirstName("Yevgen");
                person.setLastName("P");

                bibernateSession.rollbackTransaction();
            }

            try (var bibernateSession = bibernateSessionFactory.openSession()) {
                List<Person> all = bibernateSession.findAll(Person.class);
                all.stream().forEach(person -> log.info("Peson {}", person));
            }
        }
    }
}
