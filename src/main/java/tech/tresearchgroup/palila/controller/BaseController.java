package tech.tresearchgroup.palila.controller;

import com.google.gson.Gson;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Index;
import com.zaxxer.hikari.HikariDataSource;
import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;
import io.activej.serializer.BinarySerializer;
import jdk.jshell.spi.ExecutionControl;
import tech.tresearchgroup.palila.controller.cache.local.GenericLocalCAO;
import tech.tresearchgroup.palila.controller.cache.local.GenericLocalPageCAO;
import tech.tresearchgroup.palila.controller.components.DatePickerComponent;
import tech.tresearchgroup.palila.controller.database.GenericDAO;
import tech.tresearchgroup.palila.controller.search.GenericSAO;
import tech.tresearchgroup.palila.model.BaseSettings;
import tech.tresearchgroup.palila.model.BasicObjectInterface;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class BaseController extends BasicController {
    protected final GenericDAO genericDAO;
    protected final GenericLocalCAO genericLocalCAO;
    protected final GenericSAO genericSAO;
    protected final GenericLocalPageCAO genericPageCAO = new GenericLocalPageCAO();
    protected final Gson gson;
    protected final Class theClass;
    String simpleName;
    protected final BinarySerializer serializer;
    protected final Index index;
    protected final int REINDEX_BATCH_SIZE;
    protected final String SEARCH_COLUMN;
    protected final HikariDataSource hikariDataSource;
    protected final byte[] sample;

    public BaseController(HikariDataSource hikariDataSource,
                          Gson gson,
                          Client client,
                          Class theClass,
                          BinarySerializer serializer,
                          int reindexSize,
                          String searchColumn,
                          Object sample) throws Exception {
        this.hikariDataSource = hikariDataSource;
        this.genericDAO = new GenericDAO(hikariDataSource);
        if(!genericDAO.tableExists(theClass)) {
            if(!genericDAO.createSQLTables(theClass)) {
                System.out.println("Failed to create: " + theClass.getSimpleName() + " tables!");
            }
        }
        this.genericLocalCAO = new GenericLocalCAO();
        this.genericSAO = new GenericSAO(gson);
        this.gson = gson;
        this.theClass = theClass;
        this.simpleName = theClass.getSimpleName().toLowerCase();
        this.serializer = serializer;
        this.REINDEX_BATCH_SIZE = reindexSize;
        this.SEARCH_COLUMN = searchColumn;
        this.index = client.index(theClass.getSimpleName());
        this.sample = CompressionController.compress(gson.toJson(sample).getBytes());
    }

    public HttpResponse deleteAllIndexes() throws Exception {
        genericSAO.reindex(REINDEX_BATCH_SIZE, genericDAO, index, theClass);
        return ok();
    }

    public Object getFromForm(HttpRequest httpRequest) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ExecutionControl.NotImplementedException {
        Field[] fields = theClass.getDeclaredFields();
        BasicObjectInterface newObject = (BasicObjectInterface) ReflectionMethods.getNewInstance(theClass);
        for (Field field : fields) {
            Class fieldClass = field.getType();
            String data = httpRequest.getPostParameter(simpleName + "-" + field.getName());
            if(data != null) {
                Method setter = ReflectionMethods.getSetter(field, theClass, fieldClass);
                if (Date.class.equals(fieldClass)) {
                    setter.invoke(newObject, java.sql.Date.valueOf(data));
                } else if (Long.class.equals(fieldClass)) {
                    setter.invoke(newObject, Long.parseLong(data));
                } else if (Integer.class.equals(fieldClass)) {
                    setter.invoke(newObject, Integer.parseInt(data));
                } else if (String.class.equals(fieldClass)) {
                    setter.invoke(newObject, data);
                } else if (Float.class.equals(fieldClass)) {
                    setter.invoke(newObject, Float.valueOf(data));
                } else if (Byte.class.equals(fieldClass)) {
                    setter.invoke(newObject, Byte.valueOf(data));
                } else if (Character.class.equals(fieldClass)) {
                    throw new ExecutionControl.NotImplementedException("Character object parsing from form");
                } else if (Double.class.equals(fieldClass)) {
                    setter.invoke(newObject, Double.valueOf(data));
                } else if (long.class.equals(fieldClass)) {
                    setter.invoke(newObject, Long.valueOf(data));
                } else if (int.class.equals(fieldClass)) {
                    setter.invoke(newObject, Integer.parseInt(data));
                } else if (float.class.equals(fieldClass)) {
                    setter.invoke(newObject, Float.valueOf(data));
                } else if (byte.class.equals(fieldClass)) {
                    setter.invoke(newObject, Byte.valueOf(data));
                } else if (char.class.equals(fieldClass)) {
                    throw new ExecutionControl.NotImplementedException("char object parsing from form");
                } else if (boolean.class.equals(fieldClass)) {
                    setter.invoke(newObject, Boolean.valueOf(data));
                } else if (double.class.equals(fieldClass)) {
                    setter.invoke(newObject, Double.valueOf(data));
                } else if (field.getType().isEnum()) {

                } else if (field.getType().isArray()) {
                    if (BaseSettings.debug) {
                        //System.out.println("Array: " + field.getType());
                    }
                } else if (field.getType().isInterface()) {
                    if (BaseSettings.debug) {
                        //System.out.println("Interface: " + field.getType());
                    }
                } else {
                    if (BaseSettings.debug) {
                        //System.out.println("Object: " + field.getType());
                    }
                }
            }
        }
        return newObject;
    }

    public List<String> toForm(boolean editable, Object object) throws InvocationTargetException, IllegalAccessException, ExecutionControl.NotImplementedException {
        Field[] fields = theClass.getDeclaredFields();
        List<String> contentList = new LinkedList<>();
        for (Field field : fields) {
            Class fieldClass = field.getType();
            Method getter = ReflectionMethods.getGetter(field, theClass);
            Object getterData = getter.invoke(object);
            if(getterData != null) {
                if (Date.class.equals(fieldClass)) {
                    contentList.add(DatePickerComponent.render(editable, field.getName(), (String) getterData, simpleName + "-" + field.getName()).render());
                } else if (Long.class.equals(fieldClass)) {

                } else if (Integer.class.equals(fieldClass)) {

                } else if (String.class.equals(fieldClass)) {

                } else if (Float.class.equals(fieldClass)) {

                } else if (Byte.class.equals(fieldClass)) {

                } else if (Character.class.equals(fieldClass)) {
                    throw new ExecutionControl.NotImplementedException("Character object parsing from form");
                } else if (Double.class.equals(fieldClass)) {

                } else if (long.class.equals(fieldClass)) {

                } else if (int.class.equals(fieldClass)) {

                } else if (float.class.equals(fieldClass)) {

                } else if (byte.class.equals(fieldClass)) {

                } else if (char.class.equals(fieldClass)) {
                    throw new ExecutionControl.NotImplementedException("char object parsing from form");
                } else if (boolean.class.equals(fieldClass)) {

                } else if (double.class.equals(fieldClass)) {

                } else if (field.getType().isEnum()) {

                } else if (field.getType().isArray()) {
                    if (BaseSettings.debug) {
                        //System.out.println("Array: " + field.getType());
                    }
                } else if (field.getType().isInterface()) {
                    if (BaseSettings.debug) {
                        //System.out.println("Interface: " + field.getType());
                    }
                } else {
                    if (BaseSettings.debug) {
                        //System.out.println("Object: " + field.getType());
                    }
                }
            }
        }
        return contentList;
    }
}
