package step.learning;

import com.mysql.cj.jdbc.Driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Random;

public class App {
    public void run() {
        System.out.println("App works");
        Driver mysqlDriver;
        try {
            mysqlDriver = new Driver();
            DriverManager.registerDriver(mysqlDriver); // registration of mysql driver
        } catch (SQLException ex) {
            System.out.println("Driver init error: " + ex.getMessage());
            return;
        }

        var connectionString = "jdbc:mysql://localhost/java191?"; // connection string to database

        Connection connection;
        try {
            connection = DriverManager.getConnection(connectionString, "user191", "pass191");
            // connection to datababe using data of user
        } catch (SQLException ex) {
            System.out.println("DB Connection error: " + ex.getMessage());
            return;
        }

        // sql query that creates new table if it is not existed yet
        var sql = "CREATE TABLE IF NOT EXISTS randoms(" +
                "id BIGINT PRIMARY KEY," +
                "num INT NOT NULL," +
                "str VARCHAR(64) NULL" +
                ") Engine=InnoDB DEFAULT CHARSET=UTF8";

        // sql query that inserts values to the table
        var insertSql = "INSERT INTO randoms VALUES (1, 1, 'This is random string')";
        try {
            var statement = connection.createStatement();
            statement.executeUpdate(sql);
            System.out.println("Query ok");
            statement.executeUpdate(insertSql);
            System.out.println("Query ok");
        } catch (SQLException ex) {
            System.out.println("Query error:" + ex.getMessage());
        }


        try {
            connection.close(); // closes the connection to db
            DriverManager.deregisterDriver(mysqlDriver); // deregistration to db
        } catch (SQLException ignored) {
        }
    }
}
