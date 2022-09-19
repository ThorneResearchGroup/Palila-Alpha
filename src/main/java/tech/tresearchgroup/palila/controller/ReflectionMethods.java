package tech.tresearchgroup.palila.controller;

import tech.tresearchgroup.palila.model.BaseSettings;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class ReflectionMethods {
    public static Method getId(Class theClass) {
        try {
            return theClass.getMethod("getId");
        } catch (NoSuchMethodException e) {
            if (BaseSettings.debug) {
                System.out.println("Failed to execute: getId on: " + theClass.getSimpleName());
            }
        }
        return null;
    }

    public static Method getGetter(Field field, Class theClass) {
        String cap = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        try {
            if (boolean.class.equals(field.getType())) {
                return theClass.getMethod("is" + cap);
            }
            return theClass.getMethod("get" + cap);
        } catch (NoSuchMethodException e) {
            if (BaseSettings.debug) {
                System.out.println("Failed to execute: get" + cap + " on: " + theClass.getSimpleName());
            }
        }
        return null;
    }

    /**
     * Gets the getter function of an objects attribute
     *
     * @param field          the fields
     * @param theClass       the class you're targeting
     * @param parameterClass the class of the parameter you're providing (e.g. if I pass a string, I used String.class)
     * @return the method
     * @throws NoSuchMethodException if the method doesn't exist
     */
    public static Method getSetter(Field field, Class theClass, Class parameterClass) {
        String cap = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        try {
            return theClass.getMethod("set" + cap, parameterClass);
        } catch (NoSuchMethodException e) {
            if (BaseSettings.debug) {
                System.out.println("Failed to execute: set" + cap + " on: " + theClass.getSimpleName());
            }
        }
        return null;
    }

    public static Object getValueOf(Class theClass, String data) throws InvocationTargetException, IllegalAccessException {
        try {
            if (Objects.equals(data, null)) {
                return null;
            }
            return theClass.getMethod("valueOf", String.class).invoke(theClass, data);
        } catch (NoSuchMethodException e) {
            if (BaseSettings.debug) {
                System.out.println("Failed to execute: valueOf on: " + theClass.getSimpleName());
            }
        }
        return null;
    }

    public static Object getNewInstance(Class theClass) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        try {
            return theClass.getConstructors()[0].newInstance();
        } catch (ArrayIndexOutOfBoundsException e) {
            if(BaseSettings.debug) {
                System.out.println(theClass.getSimpleName() + " does not have a constructor.");
            }
        }
        return null;
    }
}
