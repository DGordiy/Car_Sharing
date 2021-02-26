package carsharing;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CarDAO implements DAO<Car> {
    private final DBEngine dbEngine;

    CarDAO(DBEngine dbEngine) {
        this.dbEngine = dbEngine;
    }

    @Override
    public void createTable() throws SQLException {
        String sql = "CREATE TABLE CAR (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "name VARCHAR UNIQUE NOT NULL," +
                "company_id INT NOT NULL," +
                "CONSTRAINT company_id FOREIGN KEY(company_id)" +
                "REFERENCES COMPANY(id)" +
                ");";

        try (Connection connection = dbEngine.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    @Override
    public List<Car> findAll() throws SQLException {
        return findByCompany(null);
    }

    public List<Car> findByCompany(Company company) throws SQLException {
        List<Car> instances = new ArrayList<>();

        String sql = company == null ? "SELECT * FROM CAR" : "SELECT * FROM CAR WHERE company_id=" + company.getId();
        try (Connection connection = dbEngine.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql + " ORDER BY id")) {
            while (rs.next()) {
                instances.add(new Car(rs.getInt("id"), rs.getString("name"), rs.getInt("company_id")));
            }
        }

        return instances;
    }

    @Override
    public void save(Car instance) throws SQLException {
        try (Connection connection = dbEngine.getConnection();
             Statement statement = connection.createStatement()) {
            String sql = String.format("INSERT INTO CAR (name, company_id) VALUES ('%s', %d);", instance.getName(), instance.getCompanyId());
            statement.executeUpdate(sql);
        }
    }

    @Override
    public Car findById(int id) throws SQLException {
        return findAll().stream()
                .filter(v -> v.getId() == id)
                .findFirst().orElse(null);
    }


}
