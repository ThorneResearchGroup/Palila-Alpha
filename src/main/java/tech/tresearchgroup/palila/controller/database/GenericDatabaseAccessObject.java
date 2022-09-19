package tech.tresearchgroup.palila.controller.database;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public interface GenericDatabaseAccessObject {
    boolean create(Object object) throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException;

    Object read(Long id, Class theClass) throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException;

    List<Object> readAll(Class theClass) throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException;

    List readPaginated(int resultCount, int page, Class theClass) throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException;

    List readNewestPaginated(int resultCount, int page, Class theClass) throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException;

    List readPopularPaginated(int resultCount, int page, Class theClass) throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException;

    boolean update(Object object) throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException;

    boolean delete(long id, Class theClass) throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException;

    Long getTotal(Class theClass) throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException;

    Long getTotalPages(int maxResultsSize, Class theClass) throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException;

    List databaseSearch(int maxResultsSize, String query, String returnColumn, String searchColumn, Class theClass) throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException;
}
