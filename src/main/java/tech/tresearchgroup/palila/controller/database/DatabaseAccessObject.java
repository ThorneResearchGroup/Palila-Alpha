package tech.tresearchgroup.palila.controller.database;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public interface DatabaseAccessObject {
    boolean create(Object object) throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException;

    Object read(Long id) throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException;

    List<Object> readAll() throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException;

    List readPaginated(int resultCount, int page) throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException;

    List readNewestPaginated(int resultCount, int page) throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException;

    List readPopularPaginated(int resultCount, int page) throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException;

    boolean update(Object object) throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException;

    boolean delete(long id) throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException;

    long getTotal() throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException;

    long getTotalPages(int maxResultsSize) throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException;

    List databaseSearch(int maxResultsSize, String query, String returnColumn, String searchColumn) throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException;
}
