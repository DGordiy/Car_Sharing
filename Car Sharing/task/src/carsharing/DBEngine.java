package carsharing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBEngine {
    private final String DB_URL;

    DBEngine(String dbName) throws ClassNotFoundException {
        Class.forName("org.h2.Driver");
        DB_URL = "jdbc:h2:C:\\Users\\Denis\\IdeaProjects\\Car Sharing\\Car Sharing\\task\\src\\carsharing\\db\\"
        //DB_URL = "jdbc:h2:./src/carsharing/db/"
                + dbName;
    }

    public Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(DB_URL);
        connection.setAutoCommit(true);
        return connection;
    }
}
