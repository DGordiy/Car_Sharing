package carsharing;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerDAO implements DAO<Customer> {
    private final DBEngine dbEngine;

    CustomerDAO(DBEngine dbEngine) {
        this.dbEngine = dbEngine;
    }

    @Override
    public void createTable() throws SQLException {
        String sql = "CREATE TABLE CUSTOMER (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "name VARCHAR UNIQUE NOT NULL," +
                "rented_car_id INT," +
                "CONSTRAINT rented_car_id FOREIGN KEY(rented_car_id)" +
                "REFERENCES CAR(id)" +
                ");";

        try (Connection connection = dbEngine.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    @Override
    public List<Customer> findAll() throws SQLException {
        String sql = "SELECT * FROM customer ORDER BY id";
        List<Customer> instances = new ArrayList<>();

        try (Connection connection = dbEngine.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                instances.add(new Customer(rs.getInt("id"), rs.getString("name"), rs.getInt("rented_car_id")));
            }
        }

        return instances;
    }

    @Override
    public void save(Customer instance) throws SQLException {
        try (Connection connection = dbEngine.getConnection();
             Statement statement = connection.createStatement()) {
            String sql = String.format("INSERT INTO CUSTOMER (name) VALUES ('%s');", instance.getName());
            statement.executeUpdate(sql);
        }
    }

    @Override
    public Customer findById(int id) throws SQLException {
        return findAll().stream()
                .filter(v -> v.getId() == id)
                .findFirst().orElse(null);
    }

    public void updateRentCarId(Customer instance, int id) throws SQLException {
        try (Connection connection = dbEngine.getConnection();
             Statement statement = connection.createStatement()) {
            String sql = String.format("UPDATE CUSTOMER SET rented_car_id=%s WHERE id=%d;", id == 0 ? null : id, instance.getId());
            statement.executeUpdate(sql);
        }
    }

    public List<Customer> findWithCar() throws SQLException {
        return findAll().stream()
                .filter(v -> v.getRented_car_id() != 0)
                .collect(Collectors.toList());
    }
}
