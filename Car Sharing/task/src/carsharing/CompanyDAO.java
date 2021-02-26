package carsharing;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CompanyDAO implements DAO<Company> {
    private final DBEngine dbEngine;

    CompanyDAO(DBEngine dbEngine) {
        this.dbEngine = dbEngine;
    }

    @Override
    public void createTable() throws SQLException {
//        String sql = "ALTER TABLE CAR " +
//                "DROP CONSTRAINT company_id;" +
//                "ALTER TABLE CUSTOMER " +
//                "DROP CONSTRAINT rented_car_id;" +
//                "DROP TABLE COMPANY;" +
//                "DROP TABLE CAR;" +
//                "DROP TABLE CUSTOMER;";
//        try (Connection connection = dbEngine.getConnection();
//             Statement statement = connection.createStatement()) {
//            statement.executeUpdate(sql);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

        String sql = "CREATE TABLE COMPANY (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "name VARCHAR UNIQUE NOT NULL)";

        try (Connection connection = dbEngine.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    @Override
    public List<Company> findAll() throws SQLException {
        String sql = "SELECT * FROM COMPANY ORDER BY id";
        List<Company> instances = new ArrayList<>();

        try (Connection connection = dbEngine.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                instances.add(new Company(rs.getInt("id"), rs.getString("name")));
            }
        }

        return instances;
    }

    @Override
    public void save(Company instance) throws SQLException {
        try (Connection connection = dbEngine.getConnection();
             Statement statement = connection.createStatement()) {
            String sql = String.format("INSERT INTO COMPANY (name) VALUES ('%s');", instance.getName());
            statement.executeUpdate(sql);
        }
    }

    public Company findById(int id) throws SQLException {
        return findAll().stream()
                .filter(v -> v.getId() == id)
                .findFirst().orElse(null);
    }

    public List<Car> findFreeCars(Company company, CustomerDAO customerDAO, CarDAO carDAO) throws SQLException {
        List<Integer> rentedCarsId = customerDAO.findWithCar().stream()
                .map(Customer::getRented_car_id)
                .collect(Collectors.toList());
        List<Car> cars = carDAO.findByCompany(company);
        return cars.stream()
                .filter(v -> !rentedCarsId.contains(v.getId()))
                .collect(Collectors.toList());
    }
}
