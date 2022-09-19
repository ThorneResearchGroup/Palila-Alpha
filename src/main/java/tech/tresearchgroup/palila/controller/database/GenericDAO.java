package tech.tresearchgroup.palila.controller.database;

import com.zaxxer.hikari.HikariDataSource;
import tech.tresearchgroup.palila.controller.ReflectionMethods;
import tech.tresearchgroup.palila.model.BaseSettings;
import tech.tresearchgroup.palila.model.BasicObjectInterface;

import java.lang.reflect.*;
import java.sql.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class GenericDAO implements GenericDatabaseAccessObject {
    private final HikariDataSource hikariDataSource;

    public GenericDAO(HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
    }

    @Override
    public boolean create(Object object) throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Class theClass = object.getClass();
        Field[] fields = theClass.getDeclaredFields();
        StringBuilder statementBuilder = new StringBuilder();
        statementBuilder.append("INSERT INTO `").append(theClass.getSimpleName()).append("` VALUES (?");
        statementBuilder.append(",?".repeat(fields.length - 1));
        statementBuilder.append(")");
        Connection connection = hikariDataSource.getConnection();
        PreparedStatement preparedStatement = prepareInsert(object, connection);
        if(BaseSettings.debug) {
            System.out.println(preparedStatement);
        }
        boolean returnThis = preparedStatement.executeUpdate() == 0;
        connection.commit();
        connection.close();
        return returnThis;
    }

    @Override
    public Object read(Long id, Class theClass) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Connection connection = hikariDataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * from " + theClass.getSimpleName() + " WHERE `id` = " + id + ";");
        if (BaseSettings.debug) {
            System.out.println(preparedStatement);
        }
        ResultSet resultSet = preparedStatement.executeQuery();
        connection.close();
        if (resultSet.next()) {
            return getFromResultSet(resultSet, theClass.getConstructors()[0].newInstance());
        }
        return null;
    }

    @Override
    public List readAll(Class theClass) throws SQLException, InvocationTargetException, NoSuchMethodException {
        Connection connection = hikariDataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * from " + theClass.getSimpleName() + "");
        if (BaseSettings.debug) {
            System.out.println(preparedStatement);
        }
        ResultSet resultSet = preparedStatement.executeQuery();
        List returnThis = getAllFromResultSet(resultSet, theClass);
        connection.close();
        return returnThis;
    }

    @Override
    public List readPaginated(int resultCount, int page, Class theClass) throws SQLException, InvocationTargetException, NoSuchMethodException {
        String statement;
        String simpleName = theClass.getSimpleName();
        if (page == 0) {
            if (resultCount == 0) {
                statement = "SELECT * FROM " + simpleName;
            } else {
                statement = "SELECT * FROM " + simpleName + " LIMIT " + resultCount;
            }
        } else {
            statement = "SELECT * FROM " + simpleName + " LIMIT " + resultCount + "," + page;
        }
        Connection connection = hikariDataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(statement);
        if (BaseSettings.debug) {
            System.out.println(preparedStatement);
        }
        preparedStatement.execute();
        ResultSet resultSet = preparedStatement.getResultSet();
        connection.close();
        return getAllFromResultSet(resultSet, theClass);
    }

    @Override
    public List readNewestPaginated(int resultCount, int page, Class theClass) throws SQLException, InvocationTargetException, NoSuchMethodException {
        String statement;
        if (page == 0) {
            if (resultCount == 0) {
                statement = "SELECT * FROM " + theClass.getSimpleName() + " ORDER BY id DESC";
            } else {
                statement = "SELECT * FROM " + theClass.getSimpleName() + " ORDER BY id DESC LIMIT " + resultCount;
            }
        } else {
            statement = "SELECT * FROM " + theClass.getSimpleName() + " ORDER BY id DESC" + resultCount + "," + (page * resultCount);
        }
        Connection connection = hikariDataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(statement);
        if (BaseSettings.debug) {
            System.out.println(preparedStatement);
        }
        preparedStatement.execute();
        ResultSet resultSet = preparedStatement.getResultSet();
        connection.close();
        return getAllFromResultSet(resultSet, theClass);
    }

    @Override
    public List readPopularPaginated(int resultCount, int page, Class theClass) throws SQLException, InvocationTargetException, NoSuchMethodException {
        String statement;
        if (page == 0) {
            if (resultCount == 0) {
                statement = "SELECT * FROM " + theClass.getSimpleName() + " ORDER BY views DESC";
            } else {
                statement = "SELECT * FROM " + theClass.getSimpleName() + " ORDER BY views DESC LIMIT " + resultCount;
            }
        } else {
            statement = "SELECT * FROM " + theClass.getSimpleName() + " ORDER BY views DESC LIMIT " + resultCount + "," + (page * resultCount);
        }
        Connection connection = hikariDataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(statement);
        if (BaseSettings.debug) {
            System.out.println(preparedStatement);
        }
        preparedStatement.execute();
        ResultSet resultSet = preparedStatement.getResultSet();
        connection.close();
        return getAllFromResultSet(resultSet, theClass);
    }

    @Override
    public boolean update(Object object) throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Class theClass = object.getClass();
        StringBuilder statementBuilder = new StringBuilder();
        Field[] fields = theClass.getDeclaredFields();
        statementBuilder.append("UPDATE ").append(theClass.getSimpleName()).append(" SET ");
        for (int i = 0 ; i != fields.length; i++) {
            statementBuilder.append(fields[i].getName()).append(" = ?");
            if(i != (fields.length - 1)) {
                statementBuilder.append(", ");
            }
        }
        Method getId = ReflectionMethods.getId(object.getClass());
        Long id = (Long) getId.invoke(object);
        statementBuilder.append(" WHERE id = " + id);
        Connection connection = hikariDataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(statementBuilder.toString());
        addFields(fields, theClass, object, preparedStatement);
        if (BaseSettings.debug) {
            System.out.println(preparedStatement);
        }
        boolean returnThis = preparedStatement.executeUpdate() == 0;
        connection.commit();
        connection.close();
        return returnThis;
    }

    @Override
    public boolean delete(long id, Class theClass) throws SQLException {
        StringBuilder statementBuilder = new StringBuilder();
        statementBuilder.append("DELETE FROM `").append(theClass.getSimpleName()).append("` ");
        statementBuilder.append("WHERE ").append("id").append("=?");
        statementBuilder.append(")");
        Connection connection = hikariDataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(statementBuilder.toString());
        preparedStatement.setLong(1, id);
        if (BaseSettings.debug) {
            System.out.println(preparedStatement);
        }
        boolean returnThis = preparedStatement.execute();
        connection.commit();
        connection.close();
        return returnThis;
    }

    @Override
    public Long getTotal(Class theClass) throws SQLException {
        StringBuilder statementBuilder = new StringBuilder();
        statementBuilder.append("SELECT COUNT(*) AS COUNT FROM `").append(theClass.getSimpleName()).append("`");
        Connection connection = hikariDataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(statementBuilder.toString());
        if (BaseSettings.debug) {
            System.out.println(preparedStatement);
        }
        preparedStatement.execute();
        ResultSet resultSet = preparedStatement.getResultSet();
        if (resultSet.next()) {
            Long returnThis = resultSet.getLong("COUNT");
            connection.close();
            return returnThis;
        }
        connection.close();
        return null;
    }

    @Override
    public Long getTotalPages(int maxResultsSize, Class theClass) throws SQLException {
        long total = getTotal(theClass);
        if (total != 0) {
            return total / maxResultsSize;
        }
        return 0L;
    }

    @Override
    public List databaseSearch(int maxResultsSize, String query, String returnColumn, String searchColumn, Class theClass) throws SQLException, InvocationTargetException, NoSuchMethodException {
        String statement;
        if (returnColumn.equals("*")) {
            statement = "SELECT * FROM " + theClass.getSimpleName() + " where " + searchColumn + " LIKE '%" + query + "%' LIMIT " + maxResultsSize;
        } else {
            statement = "SELECT " + returnColumn + " from " + theClass.getSimpleName() + " where " + searchColumn + " LIKE '%" + query + "%' LIMIT " + maxResultsSize;
        }
        Connection connection = hikariDataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(statement);
        if (BaseSettings.debug) {
            System.out.println(preparedStatement);
        }
        preparedStatement.execute();
        ResultSet resultSet = preparedStatement.getResultSet();
        connection.close();
        return getAllFromResultSet(resultSet, theClass);
    }

    public PreparedStatement prepareInsert(Object object, Connection connection) throws SQLException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class theClass = object.getClass();
        StringBuilder statementBuilder = new StringBuilder();
        Field[] fields = theClass.getDeclaredFields();
        statementBuilder.append("INSERT INTO `").append(theClass.getSimpleName()).append("` VALUES (?");
        statementBuilder.append(",?".repeat(fields.length - 1));
        statementBuilder.append(")");
        PreparedStatement preparedStatement = connection.prepareStatement(statementBuilder.toString());
        addFields(fields, theClass, object, preparedStatement);
        return preparedStatement;
    }

    public void addFields(Field[] fields, Class theClass, Object object, PreparedStatement preparedStatement) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException {
        int id = 1;
        for (Field field : fields) {
            Class fieldClass = field.getType();
            Method method = ReflectionMethods.getGetter(field, theClass);
            Object fieldObject = method.invoke(object);
            if(fieldObject == null) {
                preparedStatement.setNull(id, Types.NULL);
            } else {
                if (Date.class.equals(fieldClass)) {
                    Date date = (Date) fieldObject;
                    if (date != null) {
                        preparedStatement.setDate(id, new java.sql.Date(date.getTime()));
                    } else {
                        preparedStatement.setDate(id, null);
                    }
                } else if (Long.class.equals(fieldClass)) {
                    preparedStatement.setLong(id, (Long) fieldObject);
                } else if (Integer.class.equals(fieldClass)) {
                    preparedStatement.setInt(id, (Integer) fieldObject);
                } else if (String.class.equals(fieldClass)) {
                    preparedStatement.setString(id, (String) fieldObject);
                } else if (Float.class.equals(fieldClass)) {
                    preparedStatement.setFloat(id, (Float) fieldObject);
                } else if (Byte.class.equals(fieldClass)) {
                    preparedStatement.setByte(id, (Byte) fieldObject);
                } else if (Character.class.equals(fieldClass)) {
                    preparedStatement.setString(id, String.valueOf(fieldObject));
                } else if (Double.class.equals(fieldClass)) {
                    preparedStatement.setDouble(id, (Double) fieldObject);
                } else if (long.class.equals(fieldClass)) {
                    preparedStatement.setLong(id, (long) fieldObject);
                } else if (int.class.equals(fieldClass)) {
                    preparedStatement.setInt(id, (int) fieldObject);
                } else if (float.class.equals(fieldClass)) {
                    preparedStatement.setFloat(id, (Float) fieldObject);
                } else if (byte.class.equals(fieldClass)) {
                    preparedStatement.setByte(id, (byte) fieldObject);
                } else if (char.class.equals(fieldClass)) {
                    preparedStatement.setString(id, String.valueOf((char) fieldObject));
                } else if (boolean.class.equals(fieldClass)) {
                    preparedStatement.setBoolean(id, (boolean) fieldObject);
                } else if (double.class.equals(fieldClass)) {
                    preparedStatement.setDouble(id, (double) fieldObject);
                } else if (fieldClass.isEnum()) {
                    preparedStatement.setString(id, String.valueOf(fieldObject));
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
            id++;
        }
    }

    public List<BasicObjectInterface> getAllFromResultSet(ResultSet resultSet, Class theClass) throws SQLException {
        List<BasicObjectInterface> objects = new LinkedList<>();
        try {
            List interfaceFields = null;
            while (resultSet.next()) {
                BasicObjectInterface object = (BasicObjectInterface) ReflectionMethods.getNewInstance(theClass);
                Field[] fields = object.getClass().getDeclaredFields();
                if (interfaceFields == null) {
                    interfaceFields = applySingularFieldsAndGetObjects(resultSet, object, fields);
                } else {
                    applySingularFieldsAndGetObjects(resultSet, object, fields);
                }
                objects.add(object);
            }
            if(interfaceFields != null) {
                addInterfaceFieldsToObject(objects, interfaceFields);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return objects;
    }

    public List<Long> getIdsFromResultSet(ResultSet resultSet) throws SQLException {
        List<Long> objects = new LinkedList<>();
        while (resultSet.next()) {
            objects.add(resultSet.getLong("id"));
        }
        return objects;
    }

    public Object getFromResultSet(ResultSet resultSet, Object object) throws InvocationTargetException, IllegalAccessException, SQLException, InstantiationException {
        Field[] fields = object.getClass().getDeclaredFields();
        List interfaceFields = applySingularFieldsAndGetObjects(resultSet, object, fields);
        List list = new LinkedList();
        list.add(object);
        addInterfaceFieldsToObject(list, interfaceFields);
        return list.get(0);
    }

    public List applySingularFieldsAndGetObjects(ResultSet resultSet, Object object, Field[] fields) {
        List<Field> interfaceFields = new LinkedList<>();
        for (Field field : fields) {
            try {
                Class fieldClass = field.getType();
                Method method = ReflectionMethods.getSetter(field, object.getClass(), fieldClass);
                if (Date.class.equals(fieldClass)) {
                    method.invoke(object, resultSet.getDate(field.getName()));
                } else if (Long.class.equals(fieldClass)) {
                    method.invoke(object, resultSet.getLong(field.getName()));
                } else if (Integer.class.equals(fieldClass)) {
                    method.invoke(object, resultSet.getInt(field.getName()));
                } else if (String.class.equals(fieldClass)) {
                    method.invoke(object, resultSet.getString(field.getName()));
                } else if (Float.class.equals(fieldClass)) {
                    method.invoke(object, resultSet.getFloat(field.getName()));
                } else if (Byte.class.equals(fieldClass)) {
                    method.invoke(object, resultSet.getByte(field.getName()));
                } else if (Character.class.equals(fieldClass)) {
                    method.invoke(object, resultSet.getString(field.getName()));
                } else if (Double.class.equals(fieldClass)) {
                    method.invoke(object, resultSet.getDouble(field.getName()));
                } else if (long.class.equals(fieldClass)) {
                    method.invoke(object, resultSet.getLong(field.getName()));
                } else if (int.class.equals(fieldClass)) {
                    method.invoke(object, resultSet.getInt(field.getName()));
                } else if (float.class.equals(fieldClass)) {
                    method.invoke(object, resultSet.getFloat(field.getName()));
                } else if (byte.class.equals(fieldClass)) {
                    method.invoke(object, resultSet.getByte(field.getName()));
                } else if (char.class.equals(fieldClass)) {
                    method.invoke(object, resultSet.getString(field.getName()));
                } else if (boolean.class.equals(fieldClass)) {
                    method.invoke(object, resultSet.getBoolean(field.getName()));
                } else if (double.class.equals(fieldClass)) {
                    method.invoke(object, resultSet.getDouble(field.getName()));
                } else if (field.getType().isEnum()) {
                    String string = resultSet.getString(field.getName());
                    Object valueMethod = ReflectionMethods.getValueOf(fieldClass, string);
                    if (valueMethod != null) {
                        method.invoke(object, valueMethod);
                    }
                } else if (field.getType().isArray()) {
                    if (BaseSettings.debug) {
                        //System.out.println("Array: " + field.getType());
                    }
                } else if (field.getType().isInterface()) {
                    interfaceFields.add(field);
                } else {
                    if (BaseSettings.debug) {
                        Long id = resultSet.getLong(field.getName());
                        if (id != 0) {
                            BasicObjectInterface basicObject = (BasicObjectInterface) ReflectionMethods.getNewInstance(field.getType());
                            basicObject.setId(id);
                            method.invoke(object, basicObject);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return interfaceFields;
    }

    public void addInterfaceFieldsToObject(List<BasicObjectInterface> objects, List<Field> fields) throws InvocationTargetException, IllegalAccessException, SQLException, InstantiationException {
        if (fields.size() == 0) {
            return;
        }
        StringBuilder selectString = new StringBuilder();
        selectString.append("id, ");
        StringBuilder joins = new StringBuilder();
        String declaringClassName = fields.get(0).getDeclaringClass().getSimpleName();
        for (int i = 0; i != fields.size(); i++) {
            Field field = fields.get(i);
            //SELECT
            selectString.append(field.getName());
            selectString.append("_id");
            if ((fields.size() - 1) != i) {
                selectString.append(", ");
            }
            //JOIN
            joins.append("LEFT JOIN ");
            joins.append(declaringClassName.toLowerCase());
            joins.append("_");
            joins.append(field.getName());
            joins.append(" ON ");
            joins.append(declaringClassName.toLowerCase());
            joins.append(".id");
            joins.append(" = ");
            joins.append(declaringClassName.toLowerCase());
            joins.append("_");
            joins.append(field.getName().toLowerCase());
            joins.append(".");
            joins.append(declaringClassName);
            joins.append("_id ");
        }
        //WHERE
        StringBuilder whereClauses = new StringBuilder();
        String declaringClassLower = declaringClassName.toLowerCase();
        whereClauses.append("WHERE ").append(declaringClassLower).append(".id = ").append(objects.get(0).getId());
        for (int i = 1; i != objects.size(); i++) {
            whereClauses.append(" OR ").append(declaringClassLower).append(".id = ").append(objects.get(i).getId());
            if (i == (objects.size() - 1)) {
                whereClauses.append(";");
            }
        }
        String statement = "SELECT " + selectString + " FROM " + declaringClassName + " " + joins + whereClauses;
        Connection connection = hikariDataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(statement);
        if (BaseSettings.debug) {
            System.out.println(preparedStatement);
        }
        preparedStatement.execute();
        ResultSet resultSet = preparedStatement.getResultSet();
        connection.close();
        //Extract to objects
        while (resultSet.next()) {
            Long objectColumnId = resultSet.getLong("id");
            Object object = null;
            for (BasicObjectInterface itObject : objects) {
                if (itObject.getId().equals(objectColumnId)) {
                    object = itObject;
                    break;
                }
            }
            for (Field field : fields) {
                Long columnId = resultSet.getLong(field.getName().toLowerCase() + "_id");
                if (columnId != 0) {
                    Method getList = ReflectionMethods.getGetter(field, object.getClass());
                    List list = (List) getList.invoke(object);
                    if (list == null) {
                        list = new LinkedList();
                    }
                    try {
                        ParameterizedType pt = (ParameterizedType) field.getGenericType();
                        Type subType = pt.getActualTypeArguments()[0];
                        BasicObjectInterface basicObjectInterface = (BasicObjectInterface) ReflectionMethods.getNewInstance(Class.forName(subType.getTypeName()));
                        basicObjectInterface.setId(columnId);
                        list.add(basicObjectInterface);
                        Method listSetter = ReflectionMethods.getSetter(field, object.getClass(), List.class);
                        listSetter.invoke(object, list);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public boolean tableExists(Class theClass) throws SQLException {
        Connection connection = hikariDataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SHOW TABLES LIKE '" + theClass.getSimpleName().toLowerCase() + "';");
        ResultSet resultSet = preparedStatement.executeQuery();
        connection.close();
        return resultSet.next();
    }

    public boolean createSQLTables(Class theClass) {
        Field[] fields = theClass.getDeclaredFields();
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS `").append(theClass.getSimpleName()).append("` (");
        for (Field field : fields) {
            Class fieldClass = field.getType();
            if (Date.class.equals(fieldClass)) {
                if(field.getName().equals("created") || field.getName().equals("updated")) {
                    sql.append("`").append(field.getName()).append("` datetime(6) NULL DEFAULT current_timestamp(6) ON UPDATE current_timestamp(6), ");
                } else {
                    sql.append("`").append(field.getName()).append("` datetime(6) NULL, ");
                }
            } else if (Long.class.equals(fieldClass) || long.class.equals(fieldClass)) {
                if(field.getName().equals("id")) {
                    sql.append("`id` bigint(20) NULL AUTO_INCREMENT, ");
                } else {
                    sql.append("`").append(field.getName()).append("` bigint(20) NULL, ");
                }
            } else if (Integer.class.equals(fieldClass) || int.class.equals(fieldClass)) {
                sql.append("`").append(field.getName()).append("` int(11) NULL, ");
            } else if (String.class.equals(fieldClass) || fieldClass.isEnum() || field.getType().isEnum()) {
                sql.append("`").append(field.getName()).append("` varchar(255) NULL, ");
            } else if (Float.class.equals(fieldClass) || float.class.equals(fieldClass)) {
                sql.append("`").append(field.getName()).append("` float NULL, ");
            } else if (Byte.class.equals(fieldClass) || byte.class.equals(fieldClass)) {
                sql.append("`").append(field.getName()).append("` binary(50) NULL, ");
            } else if (Character.class.equals(fieldClass) || char.class.equals(fieldClass)) {
                sql.append("`").append(field.getName()).append("` char(50) NULL, ");
            } else if (Double.class.equals(fieldClass) || double.class.equals(fieldClass)) {
                sql.append("`").append(field.getName()).append("` double NULL, ");
            } else if (boolean.class.equals(fieldClass)) {
                sql.append("`").append(field.getName()).append("` bit(1) NULL, ");
            } else if (field.getType().isArray()) {
                System.out.println("ARRAYS ARE UNSUPPORTED");
            } else if (field.getType().isInterface()) {
                String simpleLowerClass = theClass.getSimpleName().toLowerCase();
                String simpleLowerFieldClass = field.getName().toLowerCase();
                String typeClass = field.getClass().getSimpleName().toLowerCase();
                StringBuilder constraintTables = new StringBuilder();
                constraintTables.append("CREATE TABLE IF NOT EXISTS `").append(simpleLowerClass).append("_").append(simpleLowerFieldClass).append("` (");
                constraintTables.append("`").append(simpleLowerClass).append("_id` bigint(20) NULL,");
                constraintTables.append("`").append(typeClass).append("_id` bigint(20) NULL,");
                constraintTables.append("KEY `").append(simpleLowerClass).append("` (`").append(simpleLowerClass).append("_id`),");
                constraintTables.append("KEY `").append(simpleLowerFieldClass).append("` (`").append(typeClass).append("_id`)");
                constraintTables.append(") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
                try {
                    Connection connection = hikariDataSource.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement(constraintTables.toString());
                    preparedStatement.execute();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.err.println(sql);
                    return false;
                }
            } else {
                sql.append("`").append(field.getName()).append("` bigint(20) NULL, ");
            }
        }
        sql.append("PRIMARY KEY (`id`)");
        sql.append(") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
        try {
            Connection connection = hikariDataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql.toString());
            preparedStatement.execute();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(sql);
            return false;
        }
        return true;
    }
}