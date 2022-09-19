package tech.tresearchgroup.palila.controller;

import io.activej.http.HttpRequest;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public interface UserSpecificControllerInterface {
    Object createResponse(Object object, HttpRequest httpRequest) throws IOException, SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException;

    Object read(long id, HttpRequest httpRequest) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;

    List readPaginated(int page, int pageSize, HttpRequest httpRequest) throws SQLException, InvocationTargetException, NoSuchMethodException;

    boolean update(long id, Object object, HttpRequest httpRequest) throws Exception;

    boolean delete(long id, HttpRequest httpRequest) throws Exception;

    List search(String query, String returnColumn, HttpRequest httpRequest) throws SQLException, InvocationTargetException, NoSuchMethodException;

    List readNewestPaginated(int resultCount, int page, HttpRequest httpRequest) throws SQLException, InvocationTargetException, NoSuchMethodException;

    List readPopularPaginated(int resultCount, int page, HttpRequest httpRequest) throws SQLException, InvocationTargetException, NoSuchMethodException;

    Long getTotal(HttpRequest httpRequest) throws SQLException;

    Long getTotalPages(int maxResultsSize, HttpRequest httpRequest) throws SQLException;
}
