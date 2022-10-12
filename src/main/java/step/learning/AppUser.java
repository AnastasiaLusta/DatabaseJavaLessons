package step.learning;

import step.learning.dao.UserDAO;
import step.learning.entities.User;
import step.learning.hash.HashService;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class AppUser {
    private final HashService hashService;
    private final Connection connection;
    private final UserDAO userDAO;

    @Inject
    public AppUser(HashService hashService, Connection connection, UserDAO userDAO) {
        this.hashService = hashService;
        this.connection = connection;
        this.userDAO = userDAO;
    }

    public void run() {
        var sql = "CREATE TABLE IF NOT EXISTS Users (" +
                "    `id`    CHAR(36)     NOT NULL   COMMENT 'UUID'," +
                "    `login` VARCHAR(32)  NOT NULL," +
                "    `pass`  CHAR(40)     NOT NULL   COMMENT 'SHA-160 hash'," +
                "    `name`  TINYTEXT     NOT NULL," +
                "    PRIMARY KEY(id)" +
                " ) Engine=InnoDB  DEFAULT CHARSET = UTF8";
        try (var statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            System.out.println(sql);
            return;
        }
        var kbScanner = new Scanner(System.in);
        System.out.print("1 - Register\n2 - Log In\n -->");
        int userChoice = kbScanner.nextInt();
        switch (userChoice) {
            case 1:
                this.regUser();
                break;
            case 2:
                this.authUser();
                break;
            default:
                System.out.println("Error");
                break;
        }

    }

    private boolean authUser() {
        var kbScanner = new Scanner(System.in);
        System.out.print("Login: ");
        var login = kbScanner.nextLine();
        System.out.print("Password: ");
        var pass = kbScanner.nextLine();
        var user = userDAO.getUserByCredentialsOld(login, pass);
        if (user == null) {
            System.out.println("Access denied");
            return false;
        }
        System.out.println("Hello " + user.getName());
        return true;
    }

    private boolean regUser() {
        var kbScanner = new Scanner(System.in);
        var login = "";
        var pass = "";
        var pass2 = "";
        while (true) {
            System.out.println("Enter login: ");
            login = kbScanner.nextLine();
            if (login.equals("")) {
                System.out.println("Login error");
                continue;
            }
            if (userDAO.isLoginUsed(login)) {
                System.out.println("login is used");
                continue;
            }
            break;
        }
        while (true) {
            System.out.println("Enter the password");
            pass = kbScanner.nextLine();
            if (pass.equals("")) {
                System.out.println("pass required");
                continue;
            }
            System.out.println("Repeat password: ");
            pass2 = kbScanner.nextLine();
            if (!pass2.equals(pass)) {
                System.out.println("pass mismatch");
                continue;
            }
            break;
        }
        System.out.println("Name:");
        var name = kbScanner.nextLine();

        var user = new User();
        user.setLogin(login);
        user.setPass(pass);
        user.setName(name);
        var id = userDAO.add(user);
        if (id != null) {
            System.out.println("Registered. Id:" + id);
            return true;
        } else {
            System.out.println("Register error");
            return false;
        }
    }
}
