package com.levik.bibernate.demo;

import com.levik.bibernate.demo.statelesssession.Person;
import io.github.blyznytsiaorg.bibernate.Persistent;
import io.github.blyznytsiaorg.bibernate.transaction.TransactionalDatasource;
import lombok.extern.slf4j.Slf4j;
import java.sql.ResultSet;
import java.sql.SQLException;

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
