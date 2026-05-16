package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/projet_java?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "";

    private static Connection instance;

    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        try {
            if (instance == null || instance.isClosed() || !instance.isValid(2)) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                instance = DriverManager.getConnection(URL, USER, PASS);
                System.out.println("Connexion établie");
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL introuvable", e);
        }
        return instance;
    }

    public static void close() {
        try {
            if (instance != null && !instance.isClosed()) {
                instance.close();
                System.out.println("Connexion fermée");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
