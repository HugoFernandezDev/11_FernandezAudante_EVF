package pe.cert.incidentes.util;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3307/bd_incidencias?serverTimezone=UTC";
    private static final String USER = "root"; // <- cambiar si corresponde
    private static final String PASS = "gala"; // <- cambiar por tu contraseña


    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}