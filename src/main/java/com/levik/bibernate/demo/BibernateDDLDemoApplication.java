package com.levik.bibernate.demo;

import io.github.blyznytsiaorg.bibernate.Persistent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BibernateDDLDemoApplication {
    public static final String ENTITY_PACKAGE = "com.levik.bibernate.demo.ddl";

    public static void main(String[] args) {
        log.info("Bibernate Demo Application...");
        var persistent = Persistent.withDefaultConfiguration(ENTITY_PACKAGE);

        try(var bibernateEntityManager = persistent.createBibernateEntityManager()) {

            try (var bibernateSessionFactory = bibernateEntityManager.getBibernateSessionFactory()) {
                try (var bibernateSession = bibernateSessionFactory.openSession()){

                }
            }
        }
    }
}
