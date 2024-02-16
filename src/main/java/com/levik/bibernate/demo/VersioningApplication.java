package com.levik.bibernate.demo;

import com.levik.bibernate.demo.versioning.EmployeeEntity;
import io.github.blyznytsiaorg.bibernate.Persistent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VersioningApplication {
    public static final String ENTITY_PACKAGE = "com.levik.bibernate.demo.versioning";

    public static void main(String[] args) {
        var persistent = Persistent.withDefaultConfiguration(ENTITY_PACKAGE);
        try(var bibernateEntityManager = persistent.createBibernateEntityManager()) {

            try (var bibernateSessionFactory = bibernateEntityManager.getBibernateSessionFactory()) {
                try (var bibernateSession = bibernateSessionFactory.openSession()){
                    var employeeEntity = new EmployeeEntity();
                    employeeEntity.setId(1L);
                    employeeEntity.setFirstName("Vlad");
                    employeeEntity.setLastName("Mihalcea");

                    var saveEmployeeEntity = bibernateSession.save(EmployeeEntity.class, employeeEntity);
                    bibernateSession.flush();

                    log.info("Person {} ", saveEmployeeEntity);
                }
            }
        }
    }
}
