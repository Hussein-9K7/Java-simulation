package simulation.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mariadb://localhost:3306/health_center_simulation";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1234";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
