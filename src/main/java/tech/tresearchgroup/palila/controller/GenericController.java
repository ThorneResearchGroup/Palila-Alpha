package tech.tresearchgroup.palila.controller;

import com.google.gson.Gson;
import com.meilisearch.sdk.Client;
import com.zaxxer.hikari.HikariDataSource;
import io.activej.http.HttpRequest;
import io.activej.serializer.BinarySerializer;
import tech.tresearchgroup.palila.model.BaseSettings;
import tech.tresearchgroup.palila.model.enums.CacheTypesEnum;
import tech.tresearchgroup.palila.model.enums.PermissionGroupEnum;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public class GenericController extends BaseController implements GenericControllerInterface {
    private final PermissionGroupEnum CREATE_PERMISSION_LEVEL;
    private final PermissionGroupEnum READ_PERMISSION_LEVEL;
    private final PermissionGroupEnum UPDATE_PERMISSION_LEVEL;
    private final PermissionGroupEnum DELETE_PERMISSION_LEVEL;
    private final PermissionGroupEnum SEARCH_PERMISSION_LEVEL;
    private final BasicUserController basicUserController;

    public GenericController(HikariDataSource hikariDataSource,
                             Gson gson,
                             Client client,
                             Class theClass,
                             BinarySerializer serializer,
                             int reindexSize,
                             String searchColumn,
                             Object sample,
                             PermissionGroupEnum createPermissionLevel,
                             PermissionGroupEnum readPermissionLevel,
                             PermissionGroupEnum updatePermissionLevel,
                             PermissionGroupEnum deletePermissionLevel,
                             PermissionGroupEnum searchPermissionLevel,
                             BasicUserController basicUserController) throws Exception {
        super(hikariDataSource, gson, client, theClass, serializer, reindexSize, searchColumn, sample);
        this.CREATE_PERMISSION_LEVEL = createPermissionLevel;
        this.READ_PERMISSION_LEVEL = readPermissionLevel;
        this.UPDATE_PERMISSION_LEVEL = updatePermissionLevel;
        this.DELETE_PERMISSION_LEVEL = deletePermissionLevel;
        this.SEARCH_PERMISSION_LEVEL = searchPermissionLevel;
        this.basicUserController = basicUserController;
    }

    @Override
    public Object createSecureResponse(Object object, HttpRequest httpRequest) throws Exception {
        if (canAccess(httpRequest, CREATE_PERMISSION_LEVEL, basicUserController)) {
            if (genericDAO.create(object)) {
                genericSAO.createDocument(object, index);
                genericPageCAO.delete();
                return object;
            }
        } else {
            return unauthorized();
        }
        return null;
    }

    @Override
    public byte[] createSecureAPIResponse(Object object, HttpRequest httpRequest) throws Exception {
        Object createdObject = createSecureResponse(object, httpRequest);
        if (createdObject != null) {
            return CompressionController.compress(gson.toJson(createdObject).getBytes());
        }
        return null;
    }

    @Override
    public Object readSecureResponse(long id, HttpRequest httpRequest) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (canAccess(httpRequest, CREATE_PERMISSION_LEVEL, basicUserController)) {
            byte[] cachedData = genericLocalCAO.read(CacheTypesEnum.DATABASE, id);
            Object object;
            if (cachedData != null) {
                if (BaseSettings.debug) {
                    System.out.println("Cache hit: " + "/v1/" + simpleName);
                }
                object = ActiveJSerializer.deserialize(cachedData, serializer);
            } else {
                if (BaseSettings.debug) {
                    System.out.println("Cache miss: " + "/v1/" + simpleName);
                }
                object = genericDAO.read(id, theClass);
                if (object != null) {
                    byte[] binary = ActiveJSerializer.serialize(object, serializer);
                    genericLocalCAO.create(CacheTypesEnum.DATABASE, id, binary);
                }
            }
            return object;
        } else {
            return unauthorized();
        }
    }

    @Override
    public byte[] readSecureAPIResponse(long id, HttpRequest httpRequest) throws IOException, SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        byte[] cacheData = genericLocalCAO.read(CacheTypesEnum.API, id);
        if (cacheData != null) {
            if (BaseSettings.debug) {
                System.out.println("Cache hit!");
            }
            return cacheData;
        }
        Object readObject = readSecureResponse(id, httpRequest);
        if (readObject != null) {
            if (BaseSettings.debug) {
                System.out.println("Cache miss: " + "/v1/" + simpleName);
            }
            byte[] compressed = CompressionController.compress(gson.toJson(readObject).getBytes());
            genericLocalCAO.create(CacheTypesEnum.API, id, compressed);
            return compressed;
        }
        return null;
    }

    @Override
    public List readPaginatedResponse(int page, int pageSize) throws SQLException, InvocationTargetException, NoSuchMethodException {
        return genericDAO.readPaginated(pageSize, page, theClass);
    }

    @Override
    public byte[] readPaginatedAPIResponse(int page, int pageSize) throws SQLException, InvocationTargetException, NoSuchMethodException, IOException {
        byte[] compressed = genericPageCAO.read("/v1/" + simpleName, page, pageSize);
        if (compressed != null) {
            if (BaseSettings.debug) {
                System.out.println("Cache hit: " + "/v1/" + simpleName);
            }
            return compressed;
        }
        List readObjects = readPaginatedResponse(page, pageSize);
        if (readObjects != null) {
            if (BaseSettings.debug) {
                System.out.println("Cache miss: " + "/v1/" + simpleName);
            }
            compressed = CompressionController.compress(gson.toJson(readObjects).getBytes());
            genericPageCAO.create("/v1/" + simpleName, page, pageSize, compressed);
            return compressed;
        }
        return null;
    }

    @Override
    public boolean update(long id, Object object) throws Exception {
        if (genericDAO.update(object)) {
            byte[] json = gson.toJson(object).getBytes();
            byte[] binary = ActiveJSerializer.serialize(object, serializer);
            genericLocalCAO.update(CacheTypesEnum.DATABASE, id, binary);
            byte[] compressed = CompressionController.compress(json);
            genericLocalCAO.update(CacheTypesEnum.API, id, compressed);
            genericPageCAO.delete();
            genericSAO.updateDocument(object, index);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(long id) throws Exception {
        if (genericDAO.delete(id, theClass)) {
            genericLocalCAO.delete(id);
            genericPageCAO.delete();
            genericSAO.deleteDocument(id, index);
            return true;
        }
        return false;
    }

    @Override
    public List search(String query, String returnColumn) throws SQLException, InvocationTargetException, NoSuchMethodException {
        return genericDAO.databaseSearch(BaseSettings.maxSearchResults, query, returnColumn, SEARCH_COLUMN, theClass);
    }

    @Override
    public byte[] searchAPIResponse(String query, String returnColumn) throws SQLException, InvocationTargetException, NoSuchMethodException, IOException {
        List searchList = search(query, returnColumn);
        if (searchList != null) {
            return CompressionController.compress(gson.toJson(searchList).getBytes());
        }
        return null;
    }

    @Override
    public List readNewestPaginated(int resultCount, int page) throws SQLException, InvocationTargetException, NoSuchMethodException {
        return genericDAO.readNewestPaginated(resultCount, page, theClass);
    }

    @Override
    public byte[] readNewestPaginatedAPI(int resultCount, int page) throws IOException, SQLException, InvocationTargetException, NoSuchMethodException {
        List readPage = readNewestPaginated(resultCount, page);
        if (readPage != null) {
            return CompressionController.compress(gson.toJson(readPage).getBytes());
        }
        return null;
    }

    @Override
    public List readPopularPaginated(int resultCount, int page) throws SQLException, InvocationTargetException, NoSuchMethodException {
        return genericDAO.readPopularPaginated(resultCount, page, theClass);
    }

    public byte[] readPopularPaginatedAPI(int resultCount, int page) throws IOException, SQLException, InvocationTargetException, NoSuchMethodException {
        List readPage = readPopularPaginated(resultCount, page);
        if (readPage != null) {
            return CompressionController.compress(gson.toJson(readPage).getBytes());
        }
        return null;
    }

    @Override
    public Long getTotal() throws SQLException {
        return genericDAO.getTotal(theClass);
    }

    @Override
    public Long getTotalPages(int maxResultsSize) throws SQLException {
        return genericDAO.getTotalPages(maxResultsSize, theClass);
    }

    @Override
    public boolean reindex() {
        try {
            genericSAO.reindex(BaseSettings.maxSearchResults, genericDAO, index, theClass);
        } catch (Exception e) {
            if (BaseSettings.debug) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

    @Override
    public byte[] getSample() {
        return sample;
    }
}
