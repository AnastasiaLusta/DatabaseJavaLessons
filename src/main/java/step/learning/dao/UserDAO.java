package step.learning.dao;

import step.learning.entities.User;
import step.learning.hash.HashService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

@Singleton
public class UserDAO {
    private final HashService hashService;
    private final Connection connection;

    @Inject
    public UserDAO(HashService hashService, Connection connection) {
        this.hashService = hashService;
        this.connection = connection;
    }

    /**
     * Inserts user in DB `Users` table
     *
     * @param user data to insert
     * @return `id` of new record or null if fails
     */
    public String add(User user) {
        var id = UUID.randomUUID().toString();
        var salt = hashService.hash(UUID.randomUUID().toString());
        var passHash = this.hashPassword(user.getPass(), salt);
        var sql = "INSERT INTO Users(`id`, `login`, `pass`, `name`, `salt`) VALUES (?,?,?,?,?)";
        try (var prep = connection.prepareStatement(sql)) {
            prep.setString(1, id);
            prep.setString(2, user.getLogin());
            prep.setString(3, passHash);
            prep.setString(4, user.getName());
            prep.setString(5, salt);
            prep.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
        return id;
    }

    /**
     * Checks User table for login given
     *
     * @param login value to look for
     * @return true if login is in table
     */
    public boolean isLoginUsed(String login) {
        var sql = "SELECT COUNT(u.id) FROM Users u WHERE u.`login`=?";
        try (var prep = connection.prepareStatement(sql)) {
            prep.setString(1, login);
            var res = prep.executeQuery();
            res.next();
            return res.getInt(1) > 0;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.out.println(sql);
            return true;
        }
    }

    /**
     * Calculates hash (optionally salt) from password
     *
     * @param password open password string
     * @return hash
     */
    public String hashPassword(String password, String salt) {
        return hashService.hash(salt + password + salt);
    }

    public String hashPassword(String password) {
        return hashService.hash(password);
    }

    /**
     * Gets user from DB by login and password
     * @param login Credentials
     * @param pass Credentials
     * @return User or null if not found
     */
    public User getUserByCredentials(String login, String pass) {
        var sql = "SELECT u.* FROM Users u WHERE u.`login`=?";
        try (var prep = connection.prepareStatement(sql)) {
            prep.setString(1, login);
            var res = prep.executeQuery();
            if (res.next()) {
                var user = new User(res);
                // checks if user has password hash with salt
                if (user.getPass().equals(this.hashPassword(pass, user.getSalt()))) {
                    return user;
                }
                // checks if user has old password hash (without salt)
                else if (user.getSalt() == null && user.getPass().equals(this.hashPassword(pass))) {
                    return user;
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println(sql);
        }
        return null;
    }

    /**
     * Gets user from DB by login and password
     *
     * @param login Credentials
     * @param pass  Credentials
     * @return User or null
     */
    public User getUserByCredentialsOld(String login, String pass) {
        var sql = "SELECT u.* FROM Users u WHERE u.`login`=? AND u.`pass`=?";
        try (var prep = connection.prepareStatement(sql)) {
            prep.setString(1, login);
            prep.setString(2, pass);
            var res = prep.executeQuery();
            if (res.next()) return new User(res);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println(sql);
        }
        return null;
    }
}
