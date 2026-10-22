
package database;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final DBConnection instance = new DBConnection();

    private static final String URL =
            "jdbc:mysql://127.0.0.1:3306/bookstore_hackathon_db";

    private static final String USER = "root";

    private static final String PASSWORD = 
            System.getenv("BOOKSTORE_DB_PASSWORD");

    private DBConnection() {
        // Private constructor prevents external object creation
    }

    public static DBConnection getInstance() {
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (PASSWORD == null || PASSWORD.isBlank()) {
            throw new SQLException(
                    "Please set the BOOKSTORE_DB_PASSWORD environment variable."
            );
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}



