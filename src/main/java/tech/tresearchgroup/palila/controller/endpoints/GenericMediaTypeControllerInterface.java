package tech.tresearchgroup.palila.controller.endpoints;

import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public interface GenericMediaTypeControllerInterface {
    HttpResponse get(int page, int pageSize, HttpRequest httpRequest) throws IOException, SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException;

    HttpResponse post(String data, HttpRequest httpRequest) throws Exception;

    HttpResponse put(String data, HttpRequest httpRequest) throws Exception;

    HttpResponse getSample(HttpRequest httpRequest);

    HttpResponse getById(Long albumId, HttpRequest httpRequest) throws IOException, SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException;

    HttpResponse patch(String data, HttpRequest httpRequest) throws Exception;

    HttpResponse deleteById(Long albumId, HttpRequest httpRequest) throws Exception;

    HttpResponse deleteIndexes(HttpRequest httpRequest) throws Exception;

    HttpResponse databaseSearch(String query, String returnColumn, HttpRequest httpRequest) throws IOException, SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException;
}
