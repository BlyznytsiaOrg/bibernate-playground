package com.levik.bibernate.demo;

import com.levik.bibernate.demo.actionqueue.Person;
import io.github.blyznytsiaorg.bibernate.Persistent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActionQueueOptimizationDemoApplication {

    public static final String ENTITY_PACKAGE = "com.levik.bibernate.demo.actionqueue";

    public static void main(String[] args) {
        var persistent = Persistent.withDefaultConfiguration(ENTITY_PACKAGE);
        try(var bibernateEntityManager = persistent.createBibernateEntityManager()) {

            try (var bibernateSessionFactory = bibernateEntityManager.getBibernateSessionFactory()) {
                try (var bibernateSession = bibernateSessionFactory.openSession()){
                    var person = new Person();
                    person.setId(2L);
                    person.setFirstName("Rake");
                    person.setLastName("Tell");

                    //when
                    bibernateSession.save(Person.class, person);

                    person.setFirstName("New Rake");
                    bibernateSession.update(Person.class, person);

                    bibernateSession.delete(Person.class, person);
                    bibernateSession.flush();
                }
            }
        }
    }
}
