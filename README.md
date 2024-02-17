# Bibernate Framework Playground

## Getting Started

# Demo features:

 - **Annotation Check during Compile Face**
 - **Schema Generation**
 - **Flyway Migration Support**
 - **Automatic Persistence and Optimization**
 - **Transaction Management**
 - **Relations**
 - **Versioning**
 - **Stateless Session**

Please take into account that this is not a full supported features it is just for demo set. 
If you need more please take to look into example of the [Bibernate](https://github.com/BlyznytsiaOrg/bibernate) repo.

## Prerequisites

Before getting started with *the Bibernate*, ensure you have the following prerequisites installed:

- Java 17
- Your preferred Java IDE such as IntelliJ IDEA
- Docker or PostgresSQL

## Usage

To run the `Bibernate Playground` application:

- Clone repo
```bash
   git clone git@github.com:BlyznytsiaOrg/bibernate.git
```  
- Run docker-compose to start the PostgreSQL or skip if you have local one (db.url=jdbc:postgresql://localhost:5432/db).

```bash
   cd docker 
   docker-compose up -d
```


## Let's go Feature by Feature

- **Annotation Check during Compile Face**

Imagine you've recently started learning *the Bibernate*, but you've forgotten to include a crucial annotation in one of your entity classes. 
Time is of the essence, so it's vital to identify and resolve any errors quickly. 
Due to our focus on efficiency, encountering compile errors in this scenario is expected.

We have simple entity, but we forget to add `@Id` annotation.

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

<img width="1300" alt="image" src="https://github.com/BlyznytsiaOrg/bring/assets/73576438/b35b7a12-7403-4ef7-b768-de43dfe26ed5">

For more information please refer to [Bibernate Annotation check](https://github.com/BlyznytsiaOrg/bibernate/blob/main/features/AnnotationProcessing.md)

- **Schema Generation** 

Offers a tool for generating database schemas based on entity mappings, simplifying database setup and migration.

To enable this feature in *the Bibernate*, you need to create a `bibernate.properties` file with the following configuration settings.

```properties
bibernate.2ddl.auto=create
```


Example of the code with complex entities.

```java
@Getter
@Setter
@Entity
@Table(name = "authors", indexes = {@Index(columnList = "name")})
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(nullable = false)
    private String name;

    @CreationTimestamp
    private OffsetDateTime createdAt;

    @OneToOne(mappedBy = "author")
    AuthorProfile authorProfile;

    @ManyToMany(mappedBy = "authors")
    List<Book> books = new ArrayList<>();
}
```

```java
@Entity
@Table(name = "author_profiles")
public class AuthorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "text")
    private String aboutMe;

    @OneToOne
    @JoinColumn(name = "author_id")
    private Author author;

    @OneToMany(mappedBy = "author")
    private List<Phone> phones = new ArrayList<>();
}
```

```java
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
```

```java
@Getter
@Setter
@Entity
@Table(indexes = {@Index(name = "phone_idx", columnList = "companyNumber")})
public class Phone {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "phone_gen")
    @SequenceGenerator(name = "phone_gen", sequenceName = "phone_seq",
            initialValue = 10, allocationSize = 20)
    private Long id;

    @Column(unique = true, nullable = false)
    private String mobileNumber;
    
    private String companyNumber;
    
    @ManyToOne
    @JoinColumn(name = "author_profile_id", foreignKey = @ForeignKey(name = "FK_phone_author_profile"))
    private AuthorProfile authorProfile;
}
```

run `BibernateDDLDemoApplication`

```java
import io.github.blyznytsiaorg.bibernate.Persistent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BibernateDDLDemoApplication {
    public static final String ENTITY_PACKAGE = "com.levik.bibernate.demo.ddl";

    public static void main(String[] args) {
        log.info("Bibernate Demo Application...");
        var persistent = Persistent.withDefaultConfiguration(ENTITY_PACKAGE);
    }
}
```

For more information please refer to [Schema Generation](https://github.com/BlyznytsiaOrg/bibernate/blob/main/features/SchemaGeneration.md)


The default logging level in *the Bibernate* is INFO. To enable a lower level of logging, use a logback.xml configuration file.
Here's an example of configuring trace logging:

```log
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

We would like to highlight a few useful items in the logs.

- *The Bibernate* use the `EntityMetadataCollector` class to gather all entities using reflection and subsequently utilize the collected data. 
Then print the number of entities found. If you don't have any entities you will see and errors.

```log
15:56:42.588 [main] TRACE i.g.b.b.e.m.EntityMetadataCollector - Found entities size 0
Exception in thread "main" io.github.blyznytsiaorg.bibernate.exception.EntitiesNotFoundException: Cannot find any entities on classpath with this package com.levik.bibernate.demo.entity
at io.github.blyznytsiaorg.bibernate.entity.metadata.EntityMetadataCollector.collectMetadata(EntityMetadataCollector.java:80)
at io.github.blyznytsiaorg.bibernate.Persistent.<init>(Persistent.java:93)
at io.github.blyznytsiaorg.bibernate.Persistent.withDefaultConfiguration(Persistent.java:44)
at com.levik.bibernate.demo.BibernateDDLDemoApplication.main(BibernateDDLDemoApplication.java:13)
```

- DDL (Data Definition Language) operations. If you're new to DDL, you could write some entities and learn DDL syntax.

```log
20:29:15.035 [main] DEBUG i.g.b.bibernate.ddl.DDLProcessor - Bibernate: alter table if exists phone drop constraint if exists FK_phone_author_profile
20:29:15.035 [main] DEBUG i.g.b.bibernate.ddl.DDLProcessor - Bibernate: alter table if exists books_authors drop constraint if exists FK_book_book_authors
20:29:15.035 [main] DEBUG i.g.b.bibernate.ddl.DDLProcessor - Bibernate: alter table if exists books_authors drop constraint if exists FK_authors_book_authors
20:29:15.035 [main] DEBUG i.g.b.bibernate.ddl.DDLProcessor - Bibernate: alter table if exists author_profiles drop constraint if exists FK_rnww79asjwzd
20:29:15.044 [main] DEBUG i.g.b.bibernate.ddl.DDLProcessor - Bibernate: drop table if exists phone cascade
20:29:15.044 [main] DEBUG i.g.b.bibernate.ddl.DDLProcessor - Bibernate: drop table if exists authors cascade
20:29:15.044 [main] DEBUG i.g.b.bibernate.ddl.DDLProcessor - Bibernate: drop table if exists book cascade
20:29:15.044 [main] DEBUG i.g.b.bibernate.ddl.DDLProcessor - Bibernate: drop table if exists books_authors cascade
20:29:15.044 [main] DEBUG i.g.b.bibernate.ddl.DDLProcessor - Bibernate: drop table if exists author_profiles cascade
20:29:15.054 [main] DEBUG i.g.b.bibernate.ddl.DDLProcessor - Bibernate: drop sequence if exists phone_seq
20:29:15.054 [main] DEBUG i.g.b.bibernate.ddl.DDLProcessor - Bibernate: drop sequence if exists book_id_seq
20:29:15.057 [main] DEBUG i.g.b.bibernate.ddl.DDLProcessor - Bibernate: create sequence phone_seq start with 10 increment by 20
20:29:15.057 [main] DEBUG i.g.b.bibernate.ddl.DDLProcessor - Bibernate: create sequence book_id_seq start with 1 increment by 1
20:29:15.073 [main] DEBUG i.g.b.bibernate.ddl.DDLProcessor - Bibernate: create table phone (id bigint primary key, mobile_number varchar(255) not null unique, company_number varchar(255), author_profile_id bigint)
20:29:15.073 [main] DEBUG i.g.b.bibernate.ddl.DDLProcessor - Bibernate: create table authors (id serial primary key, name varchar(255) not null, created_at timestamp with time zone default now())
20:29:15.073 [main] DEBUG i.g.b.bibernate.ddl.DDLProcessor - Bibernate: create table books_authors (book_id bigint, author_id integer, primary key (book_id, author_id))
20:29:15.073 [main] DEBUG i.g.b.bibernate.ddl.DDLProcessor - Bibernate: create table book (id bigint primary key)
20:29:15.073 [main] DEBUG i.g.b.bibernate.ddl.DDLProcessor - Bibernate: create table author_profiles (id bigserial primary key, about_me text, author_id integer)
20:29:15.096 [main] DEBUG i.g.b.bibernate.ddl.DDLProcessor - Bibernate: create index phone_idx on phone (company_number)
20:29:15.096 [main] DEBUG i.g.b.bibernate.ddl.DDLProcessor - Bibernate: create index IDX_5uo3jyc336v9 on authors (name)
20:29:15.100 [main] DEBUG i.g.b.bibernate.ddl.DDLProcessor - Bibernate: alter table if exists phone add constraint FK_phone_author_profile foreign key(author_profile_id) references author_profiles
20:29:15.100 [main] DEBUG i.g.b.bibernate.ddl.DDLProcessor - Bibernate: alter table if exists books_authors add constraint FK_book_book_authors foreign key(book_id) references book
20:29:15.100 [main] DEBUG i.g.b.bibernate.ddl.DDLProcessor - Bibernate: alter table if exists books_authors add constraint FK_authors_book_authors foreign key(author_id) references authors
20:29:15.100 [main] DEBUG i.g.b.bibernate.ddl.DDLProcessor - Bibernate: alter table if exists author_profiles add constraint FK_rnww79asjwzd foreign key(author_id) references authors
```

If you require further information, please feel free to refer to the logs. Bibernate strives to guide you through any issues encountered.

```log
20:29:14.992 [main] INFO  org.reflections.Reflections - Reflections took 37 ms to scan 2 urls, producing 32 keys and 157 values
20:29:15.004 [main] INFO  org.reflections.Reflections - Reflections took 4 ms to scan 1 urls, producing 2 keys and 7 values
20:29:15.006 [main] TRACE i.g.b.b.e.m.EntityMetadataCollector - Found entities size 4
```

- **Flyway Migration Support**

Integrates seamlessly with Flyway migration tool, enabling database schema management and version control through declarative SQL migration scripts. 

- create own config file with following properties:
```properties
db.url=jdbc:postgresql://localhost:5432/db
db.user=user
db.password=password
db.maxPoolSize=10
bibernate.flyway.enabled=true
```
- write migration scripts in db.migration folder
```sql
CREATE TABLE IF NOT EXISTS persons (
                                       id bigserial primary key,
                                       first_name varchar(255),
                                       last_name varchar(255)
    );
```
run `FlywayMigrationSupportApplication`

```java
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
```
You will observe in the logs that the table is created and subsequent save operations can be performed: 

```log
7:18.745 [main] INFO  org.flywaydb.core.FlywayExecutor - Database: jdbc:postgresql://localhost:5432/db (PostgreSQL 15.3)
21:57:18.818 [main] INFO  o.f.core.internal.command.DbValidate - Successfully validated 1 migration (execution time 00:00.039s)
21:57:18.882 [main] INFO  o.f.core.internal.command.DbMigrate - Current version of schema "public": 1
21:57:18.885 [main] INFO  o.f.core.internal.command.DbMigrate - Schema "public" is up to date. No migration necessary.
21:57:18.935 [main] TRACE i.g.b.bibernate.dao.EntityDao - Save entity clazz Person
21:57:18.937 [main] TRACE i.g.b.b.s.BibernateFirstLevelCacheSession - Entity Person not found in firstLevel cache by id 1
21:57:18.969 [main] TRACE i.g.b.b.s.BibernateFirstLevelCacheSession - Created snapshot for entity Person id 1
21:57:18.970 [main] INFO  c.l.b.d.FlywayMigrationSupportApplication - Person Person(id=1, firstName=Gavin, lastName=King) 
```

For more information please refer to [Flyway Migration Support](https://github.com/BlyznytsiaOrg/bibernate/blob/main/features/FlywayMigrationSupport.md)


- **Automatic Persistence and Optimization**

Automatically manages the lifecycle of persistent objects by tracking changes and synchronizing them with the database. 
An optimization feature is also implemented, where it analyzes all actions performed within a session and optimizes them. 

For instance

1. you create a new Person -> save it
2. update it with a new name -> update
3. and delete it.

*The Bibernate* will analyze these actions and execute only the final action, which is the delete operation. 
This optimization enhances efficiency and streamlines database interactions.

run `ActionQueueOptimizationDemoApplication`

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

Logs result shows and we have just delete query. This optimization reduces database overhead and enhances performance.

```log
23:24:32.040 [main] DEBUG i.g.b.bibernate.dao.EntityDao - Query DELETE FROM persons WHERE id = ?; bindValue id=2
23:24:32.050 [main] TRACE i.g.b.bibernate.dao.EntityDao - Delete entity Person with id=2
```

If you need more detailed examples and cases regarding the behavior of the action queue and session optimization, 
you can refer to either the `ActionQueueTest` within our test suite or the official documentation. 
The test cases in `ActionQueueTest` cover various scenarios and edge cases, providing insights into how the action 
queue behaves under different conditions. Additionally, the documentation should offer explanations and examples to 
help you understand how the session optimization feature works and how to utilize it effectively in your applications.

For more information please refer to [Automatic Persistence](https://github.com/BlyznytsiaOrg/bibernate/blob/main/features/AutomaticPersistence.md)


- **Transaction Management**

Offers built-in support for managing database transactions, ensuring data integrity and consistency across multiple operations.

We have a simple Entity with ID -> Identity

`@Entity`: This annotation marks the class as an entity, indicating that it will be mapped to a database table. In this case, the Person class represents a table named "persons" in the database.
`@Table(name = "persons")`: This annotation specifies the name of the database table to which the entity is mapped. In this case, the Person class will be mapped to a table named "persons".
`@Id`: This annotation marks the field that represents the primary key of the entity. In this case, the id field is the primary key.
`@GeneratedValue(strategy = GenerationType.IDENTITY)`: This annotation specifies the generation strategy for the primary key values. IDENTITY indicates that the database will automatically generate unique primary key values. This is typically used with databases that support auto-increment columns.
`@Column(name = "column_name")`: This annotation specifies the mapping of the entity's field to the corresponding column in the database table. It allows you to customize the column name if it differs from the field name. In this case, firstName and lastName fields are mapped to columns named "first_name" and "last_name" in the "persons" table, respectively.


```java
@Entity
@Table(name = "persons")
@ToString
@Setter
@Getter
public class Person {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private Long id;

    private String firstName;

    @Column(name = "last_name")
    private String lastName;
}
```

Demo application

run `BibernateTransactionDemoApplication`

```java
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
```


logs from demo applications


```log
21:26:19.513 [main] TRACE i.g.b.b.e.m.EntityMetadataCollector - Found entities size 1
21:26:19.525 [main] DEBUG i.g.b.bibernate.ddl.DDLProcessor - Bibernate: drop table if exists persons cascade
21:26:19.536 [main] DEBUG i.g.b.bibernate.ddl.DDLProcessor - Bibernate: create table persons (id bigserial primary key, first_name varchar(255), last_name varchar(255))
21:26:19.554 [main] TRACE i.g.b.b.s.BibernateFirstLevelCacheSession - Session is closing. Performing dirty checking...
21:26:19.557 [main] DEBUG i.g.b.b.d.j.i.IdentityIdGenerator - Query INSERT INTO persons ( first_name, last_name ) VALUES ( ?, ? );
21:26:19.566 [main] TRACE i.g.b.bibernate.dao.EntityDao - Save entity clazz Person
21:26:19.566 [main] TRACE i.g.b.b.s.BibernateFirstLevelCacheSession - FirstLevelCache is clearing...
21:26:19.566 [main] TRACE i.g.b.b.s.BibernateFirstLevelCacheSession - Snapshots are clearing...
21:26:19.566 [main] TRACE i.g.b.b.s.DefaultBibernateSession - Close session...
21:26:19.567 [main] TRACE i.g.b.b.s.BibernateFirstLevelCacheSession - Session is closing. Performing dirty checking...
21:26:19.567 [main] TRACE i.g.b.b.s.BibernateFirstLevelCacheSession - FirstLevelCache is clearing...
21:26:19.567 [main] TRACE i.g.b.b.s.BibernateFirstLevelCacheSession - Snapshots are clearing...
21:26:19.567 [main] TRACE i.g.b.b.s.DefaultBibernateSession - Close session...
21:26:19.567 [main] DEBUG i.g.b.bibernate.dao.EntityDao - Query SELECT * FROM persons;
21:26:19.577 [main] TRACE i.g.b.b.s.BibernateFirstLevelCacheSession - Created snapshot for entity Person id 1
21:26:19.578 [main] INFO  c.l.b.d.BibernateTransactionDemoApplication - Peson Person(id=1, firstName=John, lastName=Smith)
21:26:19.578 [main] TRACE i.g.b.b.s.BibernateFirstLevelCacheSession - Session is closing. Performing dirty checking...
```

After reviewing the output, we observe that only one insertion into the 'person' table occurred, which aligns with expectations since the second operation was rolled back. Consequently, when querying with 'findAll', only one entity with the 'firstName' attribute set to 'John' is retrieved.


- **Relations**

Let's delve into configuring a Many-to-Many relationship between two entities, namely Person and Course.


```java
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"notes", "courses"})
@Data
@Entity
@Table(name = "persons")
public class Person {
    
    @Id
    @GeneratedValue
    private Long id;
    
    private String firstName;
    
    private String lastName;

    @OneToMany(mappedBy = "person")
    private List<Note> notes = new ArrayList<>();
    
    @ManyToMany
    @JoinTable(name = "persons_courses", // join table annotation is in the owning side (Person)
        joinColumn = @JoinColumn(name = "person_id"),
        inverseJoinColumn = @JoinColumn(name = "course_id"))
    private List<Course> courses = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "person_address_id")
    private Address address;
    
}
```


```java
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "courses")
public class Course {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    
    private String name;
    
    @ManyToMany(mappedBy = "courses")
    private List<Person> persons = new ArrayList<>();
    
    @ManyToOne
    private Author author;
    
}
```

The demo application looks like this 

run `ManyToManyDemoApplication`

```java
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

```



logs from demo applications

```log
22:12:41.674 [main] INFO  c.l.b.demo.ManyToManyDemoApplication - Person Person(id=null, firstName=Yevgen, lastName=null, address=null) courses [Course(id=null, name=Course_name1, persons=[], author=null)]
22:12:41.674 [main] TRACE i.g.b.b.s.BibernateFirstLevelCacheSession - Session is closing. Performing dirty checking...
22:12:41.678 [main] DEBUG i.g.b.b.d.j.i.SequenceIdGenerator - Generating ID for entityClass class com.levik.bibernate.demo.relations.Course
22:12:41.679 [main] DEBUG i.g.b.b.d.j.i.SequenceIdGenerator - Query select nextval('courses_id_seq');
22:12:41.685 [main] DEBUG i.g.b.b.d.j.i.SequenceIdGenerator - Next ID:[1] was fetched from db for sequence:[courses_id_seq]
22:12:41.689 [main] DEBUG i.g.b.b.d.j.i.SequenceIdGenerator - Query INSERT INTO courses ( id, name ) VALUES ( ?, ? );
22:12:41.690 [main] TRACE i.g.b.bibernate.dao.EntityDao - Save entity clazz Course
22:12:41.690 [main] DEBUG i.g.b.b.d.j.i.IdentityIdGenerator - Query INSERT INTO persons ( first_name, last_name, person_address_id ) VALUES ( ?, ?, ? );
22:12:41.694 [main] DEBUG i.g.b.b.d.j.i.AbstractGenerator - Query INSERT INTO persons_courses ( person_id, course_id ) VALUES ( ?, ? );
22:12:41.696 [main] TRACE i.g.b.bibernate.dao.EntityDao - Save entity clazz Person
22:12:41.697 [main] TRACE i.g.b.b.s.BibernateFirstLevelCacheSession - FirstLevelCache is clearing...
22:12:41.697 [main] TRACE i.g.b.b.s.BibernateFirstLevelCacheSession - Snapshots are clearing...
```


- **Versioning**

Supports versioning of entity data and implementing optimistic concurrency control.

The versioning feature in *the Bibernate* allows for the management of entity data versions, enabling optimistic concurrency control.
This feature is crucial for applications handling concurrent access to data, ensuring data integrity and consistency.


- Entity Versioning
   Entities in *the Bibernate* can be annotated with a version attribute, which automatically tracks changes to the entity.
   This version attribute is updated whenever the entity is modified.


```java
@Getter
@Setter
@Entity
@Table(name = "employees")
public class EmployeeEntity {
    @Id
    private Long id;

    private String firstName;

    private String lastName;

    @Version
    private Integer version;
}
```

Demo Application
run `VersioningApplication`

```java
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
```
logs from demo applications

```log
00:01:41.054 [main] DEBUG i.g.b.bibernate.ddl.DDLProcessor - Bibernate: create table employees (id bigint primary key, first_name varchar(255), last_name varchar(255), version integer)
00:01:41.114 [main] DEBUG i.g.b.b.d.j.identity.NoneIdGenerator - Query INSERT INTO employees ( id, first_name, last_name, version ) VALUES ( ?, ?, ?, ? );
00:01:41.115 [main] TRACE i.g.b.bibernate.dao.EntityDao - Save entity clazz EmployeeEntity
00:01:41.117 [main] TRACE i.g.b.b.s.BibernateFirstLevelCacheSession - Entity EmployeeEntity not found in firstLevel cache by id 1
00:01:41.123 [main] DEBUG i.g.b.bibernate.dao.EntityDao - Query SELECT * FROM employees WHERE id = ?; bindValues [1]
00:01:41.149 [main] TRACE i.g.b.b.s.BibernateFirstLevelCacheSession - Created snapshot for entity EmployeeEntity id 1
00:01:41.150 [main] INFO  c.l.b.demo.VersioningApplication - Person EmployeeEntity(id=1, firstName=FirstName2, lastName=LastName2, version=1) 
00:01:41.153 [main] TRACE i.g.b.b.s.BibernateFirstLevelCacheSession - Session is closing. Performing dirty checking...
00:01:41.159 [main] TRACE i.g.b.b.s.BibernateFirstLevelCacheSession - Dirty entity not found for entityKey EntityKey[clazz=class com.levik.bibernate.demo.versioning.EmployeeEntity, id=1, keyType=class java.lang.Long] no changes
00:01:41.164 [main] TRACE i.g.b.b.s.BibernateFirstLevelCacheSession - FirstLevelCache is clearing...
00:01:41.165 [main] TRACE i.g.b.b.s.BibernateFirstLevelCacheSession - Snapshots are clearing...
00:01:41.165 [main] TRACE i.g.b.b.s.DefaultBibernateSession - Close session...
```
the `EmployeeEntity` was created with version = 1

For more information please refer to [Versioning](https://github.com/BlyznytsiaOrg/bibernate/blob/main/features/Versioning.md)

- **Stateless Session**

*The Bibernate* provides the ability to retrieve the StatelessSession class, which does not utilize the first-level cache and provides access to the DataSource. 
Subsequently, the JDBC API can be utilized to interact with the data.

run `StatelessSessionApplication`

```java
@Slf4j
public class StatelessSession {
   public static final String ENTITY_PACKAGE = "com.levik.bibernate.demo.statelesssession";

   public static void main(String[] args) {
      var persistent = Persistent.withDefaultConfiguration(ENTITY_PACKAGE);

      var statelessSession = persistent.createStatelessSession();
      TransactionalDatasource dataSource = statelessSession.getDataSource();
      String saveQuery = "insert into persons (first_name, last_name) values ('Martin', 'Fowler')";
      String selectQuery = "select * from persons p where p.id = 1";
      try (var connection = dataSource.getConnection()) {
         try (var statement = connection.prepareStatement(saveQuery)) {
            statement.executeUpdate();
         }
         try (var statement = connection.prepareStatement(selectQuery)) {
            ResultSet resultSet = statement.executeQuery();
            Person person = new Person();
            while (resultSet.next()) {
               person.setId(resultSet.getLong("id"));
               person.setFirstName(resultSet.getString("first_name"));
               person.setLastName(resultSet.getString("last_name"));
            }
            log.info("Person {} ", person);
         }
      } catch (SQLException e) {
         throw new RuntimeException("Can't get the connection", e);
      }
   }
}
```
logs from demo applications

```log
23:15:35.962 [main] TRACE i.g.b.b.c.BibernateDatabaseSettings - Creating dataSource...
23:15:35.968 [main] DEBUG i.g.b.b.c.BibernateDataSource - Starting Bibernate Datasource ...
23:15:37.643 [main] DEBUG i.g.b.b.c.BibernateDataSource - Connection Pool size: 20
23:15:37.643 [main] DEBUG i.g.b.b.c.BibernateDataSource - Start completed Bibernate Datasource ...
23:15:37.796 [main] INFO  org.reflections.Reflections - Reflections took 129 ms to scan 2 urls, producing 32 keys and 159 values
23:15:37.833 [main] INFO  org.reflections.Reflections - Reflections took 10 ms to scan 1 urls, producing 2 keys and 2 values
23:15:37.838 [main] TRACE i.g.b.b.e.m.EntityMetadataCollector - Found entities size 1
23:15:37.890 [main] DEBUG i.g.b.bibernate.ddl.DDLProcessor - Bibernate: drop table if exists persons cascade
23:15:37.921 [main] DEBUG i.g.b.bibernate.ddl.DDLProcessor - Bibernate: create table persons (id bigserial primary key, first_name varchar(255), last_name varchar(255))
23:15:37.967 [main] DEBUG i.g.b.b.c.BibernateConfiguration - Load Property file bibernate.properties
23:15:37.968 [main] TRACE i.g.b.b.c.BibernateDatabaseSettings - Creating dataSource...
23:15:37.968 [main] DEBUG i.g.b.b.c.BibernateDataSource - Starting Bibernate Datasource ...
23:15:38.911 [main] DEBUG i.g.b.b.c.BibernateDataSource - Connection Pool size: 20
23:15:38.911 [main] DEBUG i.g.b.b.c.BibernateDataSource - Start completed Bibernate Datasource ...
23:15:38.943 [main] INFO  c.l.bibernate.demo.StatelessSession - Person Person(id=1, firstName=Martin, lastName=Fowler) 
```


