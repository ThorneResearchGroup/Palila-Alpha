package tech.tresearchgroup.palila.controller;

import com.google.gson.Gson;
import com.meilisearch.sdk.Client;
import com.zaxxer.hikari.HikariDataSource;
import io.activej.http.HttpRequest;
import io.activej.serializer.BinarySerializer;
import lombok.SneakyThrows;
import tech.tresearchgroup.palila.model.enums.PermissionGroupEnum;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class UserSpecificController extends GenericController implements UserSpecificControllerInterface {

    public UserSpecificController(HikariDataSource hikariDataSource,
                                  Gson gson,
                                  Client client,
                                  Class theClass,
                                  BinarySerializer serializer,
                                  int reindexSize,
                                  String searchColumn,
                                  Object object,
                                  PermissionGroupEnum createPermissionLevel,
                                  PermissionGroupEnum readPermissionLevel,
                                  PermissionGroupEnum updatePermissionLevel,
                                  PermissionGroupEnum deletePermissionLevel,
                                  PermissionGroupEnum searchPermissionLevel,
                                  BasicUserController basicUserController) throws Exception {
        super(
            hikariDataSource,
            gson,
            client,
            theClass,
            serializer,
            reindexSize,
            searchColumn,
            object,
            createPermissionLevel,
            readPermissionLevel,
            updatePermissionLevel,
            deletePermissionLevel,
            searchPermissionLevel,
            basicUserController
        );
    }

    @Override
    public Object createResponse(Object object, HttpRequest httpRequest) throws IOException, SQLException, InvocationTargetException, NoSuchMethodException {
        return null;
    }

    @Deprecated
    @SneakyThrows
    @Override
    public Object createSecureResponse(Object object, HttpRequest httpRequest) throws IOException, SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        throw new IllegalAccessException();
    }

    @Override
    public Object read(long id, HttpRequest httpRequest) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        return null;
    }

    @Deprecated
    @SneakyThrows
    @Override
    public Object readSecureResponse(long id, HttpRequest httpRequest) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        throw new IllegalAccessException();
    }

    @Override
    public List readPaginated(int page, int pageSize, HttpRequest httpRequest) throws SQLException, InvocationTargetException, NoSuchMethodException {
        return new LinkedList();
    }

    @Deprecated
    @SneakyThrows
    @Override
    public List readPaginatedResponse(int page, int pageSize) throws SQLException, InvocationTargetException, NoSuchMethodException {
        throw new IllegalAccessException();
    }

    @Override
    public boolean update(long id, Object object, HttpRequest httpRequest) throws Exception {
        return false;
    }

    @Deprecated
    @SneakyThrows
    @Override
    public boolean update(long id, Object object) throws Exception {
        throw new IllegalAccessException();
    }

    @Override
    public boolean delete(long id, HttpRequest httpRequest) throws Exception {
        return false;
    }

    @Deprecated
    @SneakyThrows
    @Override
    public boolean delete(long id) throws Exception {
        throw new IllegalAccessException();
    }

    @Override
    public List search(String query, String returnColumn, HttpRequest httpRequest) throws SQLException, InvocationTargetException, NoSuchMethodException {
        return new LinkedList();
    }

    @Deprecated
    @SneakyThrows
    @Override
    public List search(String query, String returnColumn) throws SQLException, InvocationTargetException, NoSuchMethodException {
        throw new IllegalAccessException();
    }

    @Override
    public List readNewestPaginated(int resultCount, int page, HttpRequest httpRequest) throws SQLException, InvocationTargetException, NoSuchMethodException {
        return new LinkedList();
    }

    @Deprecated
    @SneakyThrows
    @Override
    public List readNewestPaginated(int resultCount, int page) throws SQLException, InvocationTargetException, NoSuchMethodException {
        throw new IllegalAccessException();
    }

    @Override
    public List readPopularPaginated(int resultCount, int page, HttpRequest httpRequest) throws SQLException, InvocationTargetException, NoSuchMethodException {
        return new LinkedList();
    }

    @Deprecated
    @SneakyThrows
    @Override
    public List readPopularPaginated(int resultCount, int page) throws SQLException, InvocationTargetException, NoSuchMethodException {
        throw new IllegalAccessException();
    }

    @Override
    public Long getTotal(HttpRequest httpRequest) throws SQLException {
        return 0L;
    }

    @Deprecated
    @SneakyThrows
    @Override
    public Long getTotal() throws SQLException {
        throw new IllegalAccessException();
    }

    @Override
    public Long getTotalPages(int maxResultsSize, HttpRequest httpRequest) throws SQLException {
        return 0L;
    }

    @Deprecated
    @SneakyThrows
    @Override
    public Long getTotalPages(int maxResultsSize) throws SQLException {
        throw new IllegalAccessException();
    }
}
