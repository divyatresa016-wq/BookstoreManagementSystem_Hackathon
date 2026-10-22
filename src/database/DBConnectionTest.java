package database;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConnectionTest {

    public static void main(String[] args) {

        DBConnection db = DBConnection.getInstance();

        try (Connection con = db.getConnection()) {
            System.out.println("Database connected successfully!");
        } catch (SQLException e) {
            System.out.println("Connection failed!");
            e.printStackTrace();
        }
    }
}