package carsharing;

import java.sql.SQLException;
import java.util.List;

public interface DAO<T> {

    void createTable() throws SQLException;
    List<T> findAll() throws SQLException;
    void save(T instance) throws SQLException;
    T findById(int id) throws SQLException;
}
