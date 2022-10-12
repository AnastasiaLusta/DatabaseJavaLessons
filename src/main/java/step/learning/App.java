package step.learning;

import com.mysql.cj.jdbc.Driver;

import java.sql.*;
import java.util.Random;

public class App {
    public void run() {
        System.out.println("App works");
        // creation of driver instance and registration in DriverManager
        Driver mysqlDriver;
        try {
            mysqlDriver = new Driver();
            DriverManager.registerDriver(mysqlDriver); // registration of mysql driver
        } catch (SQLException ex) {
            System.out.println("Driver init error: " + ex.getMessage());
            return;
        }

        // connection to database
        var connectionString = "jdbc:mysql://localhost/java191?"; // connection string to database

        Connection connection;
        try {
            connection = DriverManager.getConnection(connectionString, "user191", "pass191");
            // connection to datababe using data of user
        } catch (SQLException ex) {
            System.out.println("DB Connection error: " + ex.getMessage());
            return;
        }

//        classworkQueries(connection);

        // sql query that creates table if it doesn't exist
        var sql = "CREATE TABLE IF NOT EXISTS randoms2(" +
                "id VARCHAR(64) PRIMARY KEY," +
                "num INT NOT NULL," +
                "str VARCHAR(64) NULL," +
                "dt DATETIME NULL" +
                ") Engine=InnoDB DEFAULT CHARSET=UTF8";

        // execution of sql query
        try (var statement = connection.createStatement()) {
            statement.executeUpdate(sql);
            System.out.println("Query ok");
        } catch (SQLException ex) {
            System.out.println("Query error:" + ex.getMessage());
            return;
        }

        // insertion of data into table with random values
        var rand = new Random();
        var randomNumber = rand.nextInt();
        var randomStr = "Str " + rand.nextInt();
        var randomDate = new Date(System.currentTimeMillis());
        var prepSql = "INSERT INTO randoms2 VALUES ( UUID(), ?, ?, ? )";
        try (var prep = connection.prepareStatement(prepSql)) {
            for (int i = 100500; i < 100510; ++i) {
                randomStr = "Prep " + rand.nextInt();
                prep.setInt(1, i);
                prep.setString(2, randomStr);
                prep.setDate(3, randomDate);
                prep.executeUpdate();
            }
            System.out.println("Prep ok");
        } catch (SQLException ex) {
            System.out.println("Query error: " + ex.getMessage());
            System.out.println(sql);
            return;
        }

        // selection of data from table
        var selectSql = "SELECT * FROM randoms2";
        try (var statement = connection.createStatement()) {
            var res = statement.executeQuery(selectSql);
            while (res.next()) {
                System.out.printf("%s %d %s %s%n", res.getString(1), res.getInt(2), res.getString("str"), res.getDate("dt").toString());
            }
            res.close();
            System.out.println("Query ok");
        } catch (SQLException ex) {
            System.out.println("Query error:" + ex.getMessage());
        }

        // closing of connection to database
        try {
            connection.close(); // closes the connection to db
            DriverManager.deregisterDriver(mysqlDriver); // deregistration to db
        } catch (SQLException ignored) {
        }
    }

    public void classworkQueries(Connection connection) {
        // region classwork
        // sql query that creates new table if it is not existed yet
        var sql = "CREATE TABLE IF NOT EXISTS randoms(" +
                "id BIGINT PRIMARY KEY," +
                "num INT NOT NULL," +
                "str VARCHAR(64) NULL" +
                ") Engine=InnoDB DEFAULT CHARSET=UTF8";

        var rand = new Random();
        int randomNumber = rand.nextInt();
        String randomStr = "Str " + rand.nextInt();
        // sql query that inserts values to the table
        var insertSql = String.format("INSERT INTO randoms VALUES (uuid_short(), %d, '%s')", randomNumber, randomStr);
        try (var statement = connection.createStatement()) {
            statement.executeUpdate(insertSql);
            System.out.println("Query ok");
        } catch (SQLException ex) {
            System.out.println("Query error:" + ex.getMessage());
        }

        var selectSql = "SELECT * FROM randoms";
        try (var statement = connection.createStatement()) {
            var res = statement.executeQuery(selectSql);
            while (res.next()) {
                System.out.printf("%d %d %s%n", res.getLong(1), res.getInt(2), res.getString("str"));
            }
            res.close();
            System.out.println("Query ok");
        } catch (SQLException ex) {
            System.out.println("Query error:" + ex.getMessage());
        }
        // endregion

        // region Positive and Negative numbers
        System.out.println("Positive numbers: ");
        var selectSqlPos = "SELECT * FROM randoms WHERE num>0";
        try (var statement = connection.createStatement()) {
            var res = statement.executeQuery(selectSqlPos);
            while (res.next()) {
                System.out.printf("%d %d %s%n", res.getLong(1), res.getInt(2), res.getString("str"));
            }
            res.close();
            System.out.println("Query ok");
        } catch (SQLException ex) {
            System.out.println("Query error:" + ex.getMessage());
        }

        System.out.println("Negative numbers: ");
        var selectSqlNeg = "SELECT * FROM randoms WHERE num<0";
        try (var statement = connection.createStatement()) {
            var res = statement.executeQuery(selectSqlNeg);
            while (res.next()) {
                System.out.printf("%d %d %s%n", res.getLong(1), res.getInt(2), res.getString("str"));
            }
            res.close();
            System.out.println("Query ok");
        } catch (SQLException ex) {
            System.out.println("Query error:" + ex.getMessage());
        }
        //endregion


//        sql = "INSERT INTO randoms VALUES ( UUID_SHORT(), ?, ? )";
//        try (var prep = connection.prepareStatement(sql)) {
//            for (int i = 100500; i < 100510; ++i) {
//                randomStr = "Prep " + rand.nextInt();
//                prep.setInt(1, i);
//                prep.setString(2, randomStr);
//                prep.executeUpdate();
//            }
//            System.out.println("Prep ok");
//        } catch (SQLException ex) {
//            System.out.println("Query error: " + ex.getMessage());
//            System.out.println(sql);
//            return;
//        }

// endregion
    }
}
