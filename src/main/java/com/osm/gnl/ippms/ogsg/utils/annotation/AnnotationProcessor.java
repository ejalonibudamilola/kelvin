package com.osm.gnl.ippms.ogsg.utils.annotation;
/**
 * @Author
 * Mustola
 */

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnotationProcessor {
    public static boolean isAllowanceField(Object object, String fieldName,  Class<? extends Annotation> var1) throws NoSuchFieldException {
        Class<?> clazz = object.getClass();
        Field field = clazz.getField(fieldName);
        return field.isAnnotationPresent(var1);
    }

    public static <T> Map<String, Field> getAllowanceFields(Object object, Class<? extends Annotation> var1) {
        Class<?> clazz = object.getClass();
        Map<String, Field> map = new HashMap<>();
        for (Field field: clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(var1)) {
                map.put(field.getName(), field);
            }
        }
        //--After finishing, get the Immediate Superclass if it has then get the Declared Fields...
        Class<?> superClass = object.getClass().getSuperclass();
        if(superClass != null && superClass.getName().startsWith("java."))
        return map;
        else
            for (Field field: superClass.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(var1)) {
                    map.put(field.getName(), field);
                }
            }
        return map;
    }

    public static <T> Map<Field, T> getAllowanceFields(Object object, Class<T> fieldClass,Class<? extends Annotation> var1) throws IllegalAccessException {
        Class<?> clazz = object.getClass();
        Map<Field, T> map = new HashMap<>();
        for (Field field: clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(var1)) {
                map.put(field, fieldClass.cast(field.get(object)));
            }
        }
        //--After finishing, get the Immediate Superclass if it has then get the Declared Fields...
        Class<?> superClass = object.getClass().getSuperclass();
        if(superClass != null && superClass.getName().startsWith("java."))
            return map;
        else
            for (Field field: superClass.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(var1)) {
                    map.put(field, fieldClass.cast(field.get(object)));
                }
            }
        return map;

    }

    /**
     * @Note This method works with Only SalaryAllowance Annotation Type.
     * @param object
     * @param fieldClass
     * @param <T>
     * @return
     * @throws IllegalAccessException
     */
    public static <T> Map<String, T> getNonZeroAllowanceAnnotations(Object object, Class<T> fieldClass) throws IllegalAccessException {
        Class<?> clazz = object.getClass();
        Map<String, T> map = new HashMap<>();
        T value;
        for (Field field: clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(SalaryAllowance.class)) {
                value = fieldClass.cast(field.get(object));
                if(value.getClass().isAssignableFrom(Double.class)) {
                    if (((Double) value).doubleValue() == 0.0D)
                        continue;
                    map.put(field.getAnnotation(SalaryAllowance.class).type(), value);
                }
            }
        }

        return map;

    }

    /**
     *
     * @param object
     * @param fieldClass
     * @param <T>
     * @return Map<String,T>
     * @note T could be Double etc. Also String is the Annotation Name.
     * @throws IllegalAccessException
     */
    public static Map<String, Object> getSalaryAllowanceAnnotations(Object object, Class  fieldClass) throws IllegalAccessException {
        Class<?> clazz = object.getClass();
        Map<String, Object> map = new HashMap<>();
        Object value;
        for (Field field: clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(SalaryAllowance.class)) {
                value = fieldClass.cast(field.get(object));
                  map.put(field.getAnnotation(SalaryAllowance.class).type(), value);

            }
        }

        return map;

    }
    public static List<String> getFieldsForTemplate(Object object,String type){
        Class<?> clazz = object.getClass();
        List<String> wRetList = new ArrayList<>();
        for (Field field: clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent( SalaryAllowance.class)) {
                wRetList.add(field.getAnnotation(SalaryAllowance.class).type());
            }
        }
        return  wRetList;
    }
    public static Map<String,String> getFieldsForExcelParsing(Object object){
        Class<?> clazz = object.getClass();
        Map<String,String> wRetList = new HashMap<>();
        for (Field field: clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent( SalaryAllowance.class)) {
                wRetList.put(field.getAnnotation(SalaryAllowance.class).type(),field.getName());
            }
        }
        return  wRetList;
    }
    public static <T> Map<Field, T> getAllowanceFieldsWithType(Object object, Class<T> fieldClass, String type) throws IllegalAccessException {
        Class<?> clazz = object.getClass();
        Map<Field, T> map = new HashMap<>();
        for (Field field: clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(PaycheckAllowance.class) &&
                    field.getAnnotation(PaycheckAllowance.class).type().equals(type)) {
                map.put(field, fieldClass.cast(field.get(object)));
            }
        }

        return map;
    }

    public static void setFieldValue(Object object, Field field, Object value) throws IllegalAccessException {
        field.setAccessible(true);
        field.set(object, value);
    }


}
