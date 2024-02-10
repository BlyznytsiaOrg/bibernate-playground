package com.levik.bibernate.demo;

import com.levik.bibernate.demo.entity.Person;
import io.github.blyznytsiaorg.bibernate.Persistent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BibernateDemoApplication {
    public static final String ENTITY_PACKAGE = "com.levik.bibernate.demo.entity";

    public static void main(String[] args) {
        log.info("Bibernate Demo Application...");
        var persistent = Persistent.withDefaultConfiguration(ENTITY_PACKAGE);

        try(var bibernateEntityManager = persistent.createBibernateEntityManager()) {

            try (var bibernateSessionFactory = bibernateEntityManager.getBibernateSessionFactory()) {
                try (var bibernateSession = bibernateSessionFactory.openSession()){
                    var person = new Person();
                    person.setId(1L);
                    person.setLastName("Ivan");
                    person.setFirstName("Petrovich");

                    bibernateSession.save(Person.class, person);


                    Person personFromDb = bibernateSession.findById(Person.class, 1L).orElseThrow();

                    log.info("Person from DB " + personFromDb);
                }
            }
        }
    }
}
