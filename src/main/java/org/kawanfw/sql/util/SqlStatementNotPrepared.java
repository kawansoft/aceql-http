package org.kawanfw.sql.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlStatementNotPrepared {

    public SqlStatementNotPrepared() {

    }

    /**
     * Gets the film title from the filmId.
     *
     * @param connection the JDBC Connection
     * @param filmId the film_id parameter for the SELECT query
     * @return the value of the title column from the filmId, or null if no film
     * is found
     * @throws SQLException if any SQL error occurs during the execution of the
     * query
     */
    public String getFilmTitle(Connection connection, int filmId) throws SQLException {
        String title = null;
        String sql = "SELECT title FROM film WHERE film_id = " + filmId;

        try (Statement statement = connection.createStatement()) {

            try (ResultSet resultSet = statement.executeQuery(sql)) {
                if (resultSet.next()) {
                    title = resultSet.getString("title");
                }
            }
        }

        return title;
    }
}
