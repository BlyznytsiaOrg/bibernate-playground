package com.levik.bibernate.demo;

import com.levik.bibernate.demo.multirelations.Address;
import com.levik.bibernate.demo.multirelations.House;
import com.levik.bibernate.demo.multirelations.Profile;
import com.levik.bibernate.demo.multirelations.User;
import io.github.blyznytsiaorg.bibernate.Persistent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class RelationsDemoApplication {
    public static final String ENTITY_PACKAGE = "com.levik.bibernate.demo.multirelations";

    @SneakyThrows
    public static void main(String[] args) {
        Persistent persistent = Persistent.withDefaultConfiguration(ENTITY_PACKAGE);

        try (var bibernateEntityManager = persistent.createBibernateEntityManager()) {
            var bibernateSessionFactory = bibernateEntityManager.getBibernateSessionFactory();
            try (var session = bibernateSessionFactory.openSession()) {

                var house = new House();
                house.setName("House name 1");

                House savedHouse = session.save(House.class, house);

                var address = new Address();
                address.setName("Name 1");
                address.setHouse(savedHouse);

                session.save(Address.class, address);

                var profile = new Profile();
                profile.setNickname("Petrushka");
                session.save(Profile.class, profile);

                var user = new User();
                user.setFirstName("Petrik");
                user.setLastName("Pyatochkin");
                user.setAddress(address);
                user.setProfile(profile);

                session.save(User.class, user);

                session.flush();
            }

            try (var bibernateSession = bibernateSessionFactory.openSession()) {
                Optional<User> optionalUser = bibernateSession.findById(User.class, 1L);
                optionalUser.ifPresent(user -> {
                    System.out.println(user.getFirstName() + " " + user.getLastName());
                    System.out.println(user.getAddress());
                    System.out.println(user.getProfile());// get lazy field
                });
            }
        }
    }
}
