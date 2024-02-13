# Bibernate Framework Playground

## Getting Started

# Demo features:

 - **Annotation check during compile face**
 - **Schema Generation**
 - **Automatic Persistence and optimization**

Please take into account that this is not a full supported features it is just for demo set. 
If you need more please take to look into example of repo [bibernate](https://github.com/BlyznytsiaOrg/bibernate).

## Prerequisites

Before getting started with Bibernate, ensure you have the following prerequisites installed:

- Java 17
- Your preferred Java IDE such as IntelliJ IDEA
- Docker or PostgreSQL

## Usage

To run the `Bibernate Playground` application:

- Clone repo
```bash
   git clone git@github.com:BlyznytsiaOrg/bibernate.git
```  
- 2. Run docker-compose to start the PostgreSQL or skip if you have local one.

```bash
   cd docker 
   docker-compose up -d
```

- Run demo application

```
com.levik.bibernate.demo.BibernateDDLDemoApplication
```  

## Let's go feature by feature

- **annotation check during compile face**

Imagine you've recently started learning Hibernate, but you've forgotten to include a crucial annotation in one of your entity classes. 
Time is of the essence, so it's vital to identify and resolve any errors quickly. 
Due to our focus on efficiency, encountering compile errors in this scenario is expected.

We have simple entity, but we forget to add @Id annotation.

```java
import io.github.blyznytsiaorg.bibernate.annotation.Entity;
import io.github.blyznytsiaorg.bibernate.annotation.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "persons")
public class Person {
    private Long id;

    private String firstName;

    private String lastName;
}
```

In this scenario, you'll encounter compile errors that come with clear instructions on how to resolve them. Isn't that convenient?
Isn't that convenient?

<img width="1300" alt="image" src="https://github.com/BlyznytsiaOrg/bring/assets/73576438/b35b7a12-7403-4ef7-b768-de43dfe26ed5">

We have many more check please refer [bibernate annotation check]()

- **Schema Generation** (Offers tools for generating database schemas based on entity mappings, simplifying database setup and migration.)

To enable this feature in Bibernate, you need to create a hibernate.properties file with the following configuration settings.

```bash
bibernate.2ddl.auto=create
```


Example of the code with one entity and save ang get operation

```java
import com.levik.bibernate.demo.actionqueue.Person;
import io.github.blyznytsiaorg.bibernate.Persistent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BibernateDDLDemoApplication {
    public static final String ENTITY_PACKAGE = "com.levik.bibernate.demo.entity";

    public static void main(String[] args) {
        log.info("Bibernate Demo Application...");
        var persistent = Persistent.withDefaultConfiguration(ENTITY_PACKAGE);

        try (var bibernateEntityManager = persistent.createBibernateEntityManager()) {

            try (var bibernateSessionFactory = bibernateEntityManager.getBibernateSessionFactory()) {
                try (var bibernateSession = bibernateSessionFactory.openSession()) {
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
```

The default logging level in Hibernate is INFO. To enable a lower level of logging, use a logback.xml configuration file.
Here's an example of configuring trace logging:

```bash
<configuration>

    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <logger name="io.github.blyznytsiaorg.bibernate" level="${io.github.blyznytsiaorg.bibernate.log.level:-TRACE}"/>

    <root level="INFO">
        <appender-ref ref="STDOUT"/>
    </root>

</configuration>
```

I'd like to highlight a few useful items in the logs.

- We utilize the EntityMetadataCollector class to gather all entities using reflection, and subsequently utilize the collected data. 
Then print the number of entities found. If you don't have any entities you will see and errors.

```bash
15:56:42.588 [main] TRACE i.g.b.b.e.m.EntityMetadataCollector - Found entities size 0
Exception in thread "main" io.github.blyznytsiaorg.bibernate.exception.EntitiesNotFoundException: Cannot find any entities on classpath with this package com.levik.bibernate.demo.entity
at io.github.blyznytsiaorg.bibernate.entity.metadata.EntityMetadataCollector.collectMetadata(EntityMetadataCollector.java:80)
at io.github.blyznytsiaorg.bibernate.Persistent.<init>(Persistent.java:93)
at io.github.blyznytsiaorg.bibernate.Persistent.withDefaultConfiguration(Persistent.java:44)
at com.levik.bibernate.demo.BibernateDDLDemoApplication.main(BibernateDDLDemoApplication.java:13)
```

- DDL operation. If you're new to DDL (Data Definition Language), you could write some entities and learn DDL syntax.

```bash
15:40:56.608 [main] DEBUG i.g.b.bibernate.ddl.DDLProcessor - Bibernate: create table persons (id bigint primary key, first_name varchar(255), last_name varchar(255))
```

This is the main part. If you require further information, please feel free to refer to the logs. Bibernate strives to guide you through any issues encountered.

```bash
15:40:56.575 [main] TRACE i.g.b.b.e.m.EntityMetadataCollector - Found entities size 1
15:40:56.588 [main] DEBUG i.g.b.bibernate.ddl.DDLProcessor - Bibernate: drop table if exists persons cascade
15:40:56.608 [main] DEBUG i.g.b.bibernate.ddl.DDLProcessor - Bibernate: create table persons (id bigint primary key, first_name varchar(255), last_name varchar(255))
15:40:56.635 [main] TRACE i.g.b.bibernate.dao.EntityDao - Save entity clazz Person
15:40:56.636 [main] TRACE i.g.b.b.s.BibernateFirstLevelCacheSession - Entity Person not found in firstLevel cache by id 1
15:40:56.650 [main] TRACE i.g.b.b.s.BibernateFirstLevelCacheSession - Created snapshot for entity Person id 1
15:40:56.650 [main] INFO  c.l.b.demo.BibernateDemoApplication - Person from DB Person(id=1, firstName=Petrovich, lastName=Ivan)
```

We have more complex example with all relation. go to test.


- **Automatic Persistence and optimization**

Automatically manages the lifecycle of persistent objects by tracking changes and synchronizing them with the database. 
An optimization feature is also implemented, where it analyzes all actions performed within a session and optimizes them. 

For instance

1. you create a new Person -> save it
2. update it with a new name -> update
3. and delete it.

Bibernate will intelligently analyze these actions and execute only the final action, which is the delete operation. 
This optimization enhances efficiency and streamlines database interactions.


```java
public class ActionQueueOptimizationDemoApplication {

    public static final String ENTITY_PACKAGE = "com.levik.bibernate.demo.entity";

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
```

logs result and we have just delete query. This optimization significantly reduces database overhead and enhances performance.

```bash
23:24:32.040 [main] DEBUG i.g.b.bibernate.dao.EntityDao - Query DELETE FROM persons WHERE id = ?; bindValue id=2
23:24:32.050 [main] TRACE i.g.b.bibernate.dao.EntityDao - Delete entity Person with id=2
```

If you need more detailed examples and cases regarding the behavior of the action queue and session optimization, 
you can refer to either the ActionQueueTest within our test suite or the official documentation. 
The test cases in ActionQueueTest cover various scenarios and edge cases, providing insights into how the action 
queue behaves under different conditions. Additionally, the documentation should offer explanations and examples to 
help you understand how the session optimization feature works and how to utilize it effectively in your applications.














Furthermore, Biberate accommodates a plethora of additional features.:



