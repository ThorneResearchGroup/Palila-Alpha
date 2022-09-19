package tech.tresearchgroup.palila.controller;

import io.activej.http.HttpRequest;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public interface GenericControllerInterface {
    Object createSecureResponse(Object object, HttpRequest httpRequest) throws Exception;

    byte[] createSecureAPIResponse(Object object, HttpRequest httpRequest) throws Exception;

    Object readSecureResponse(long id, HttpRequest httpRequest) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;

    byte[] readSecureAPIResponse(long id, HttpRequest httpRequest) throws IOException, SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;

    List readPaginatedResponse(int page, int pageSize) throws SQLException, InvocationTargetException, NoSuchMethodException;

    byte[] readPaginatedAPIResponse(int page, int pageSize) throws SQLException, InvocationTargetException, NoSuchMethodException, IOException;

    boolean update(long id, Object object) throws Exception;

    boolean delete(long id) throws Exception;

    List search(String query, String returnColumn) throws SQLException, InvocationTargetException, NoSuchMethodException;

    byte[] searchAPIResponse(String query, String returnColumn) throws SQLException, InvocationTargetException, NoSuchMethodException, IOException;

    List readNewestPaginated(int resultCount, int page) throws SQLException, InvocationTargetException, NoSuchMethodException;

    byte[] readNewestPaginatedAPI(int resultCount, int page) throws IOException, SQLException, InvocationTargetException, NoSuchMethodException;

    List readPopularPaginated(int resultCount, int page) throws SQLException, InvocationTargetException, NoSuchMethodException;

    Long getTotal() throws SQLException;

    Long getTotalPages(int maxResultsSize) throws SQLException;

    boolean reindex() throws Exception;

    Object getSample();
}
