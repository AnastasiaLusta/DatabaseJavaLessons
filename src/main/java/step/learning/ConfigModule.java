package step.learning;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.cj.jdbc.Driver;
import step.learning.hash.HashService;
import step.learning.hash.Sha1HashService;

public class ConfigModule extends AbstractModule {
    private Connection connection;
    private Driver mySqlDriver;

    @Override
    protected void configure() {
        bind(HashService.class).to(Sha1HashService.class);
    }

    @Provides
    Connection getConnection() throws SQLException {
        if (connection == null) {
            mySqlDriver = new Driver();
            DriverManager.registerDriver(mySqlDriver);
            var connectionString = "jdbc:mysql://localhost/java191?"; // connection string to database
            connection = DriverManager.getConnection(connectionString, "user191", "pass191");

        }
        return connection;
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception ignored) {
            }
        }
        if (mySqlDriver != null)
            try {
                DriverManager.deregisterDriver(mySqlDriver);
            } catch (Exception ignored) {
            }
    }
}