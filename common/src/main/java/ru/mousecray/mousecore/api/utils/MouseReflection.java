/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.utils;

import ru.mousecray.mousecore.api.VariableValue;
import ru.mousecray.mousecore.api.log.LogPrefixes;
import ru.mousecray.mousecore.api.log.MouseLogger;
import sun.misc.Unsafe;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class MouseReflection {
    private static final Unsafe UNSAFE;

    static {
        Unsafe unsafe = null;
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
        } catch (Exception ignored) { }
        UNSAFE = unsafe;
    }

    private final           Class<?>    clazz;
    @Nullable private final MouseLogger logger;

    private MouseReflection(Class<?> clazz, @Nullable MouseLogger logger) {
        this.clazz = clazz;
        this.logger = logger;
    }

    public static MouseReflection prepare(Class<?> clazz, @Nullable MouseLogger logger) { return new MouseReflection(clazz, logger); }
    public static MouseReflection prepare(Class<?> clazz)                               { return prepare(clazz, null); }

    @SuppressWarnings("unchecked")
    public <T> List<T> getPublicStaticFields() {
        List<T> result = new ArrayList<>();
        for (Field field : clazz.getFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                field.trySetAccessible();
                try {
                    result.add((T) field.get(null));
                } catch (IllegalAccessException e) {
                    if (logger != null) logger.atFatal(e).withPrefix(LogPrefixes.REFLECTION).log("While getting fields an error occurring");
                    throw new RuntimeException(e);
                }
            }
        }
        return result;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static <T, D> boolean setField(
            Class<? extends T> clazz, Class<D> fieldType,
            String fieldName, @Nullable T instance, @Nullable D value,
            @Nullable MouseLogger logger
    ) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            if (field.getType() != fieldType) {
                throw new RuntimeException(
                        "Field '" + fieldName + "' found, but that type incompatible. Actual type '" +
                                field.getType() + "'; Expected type '" + fieldType + "'"
                );
            }

            field.trySetAccessible();

            if (Modifier.isFinal(field.getModifiers())) setFieldViaUnsafe(field, instance, value);
            else field.set(instance, value);
            return true;
        } catch (Exception e) {
            if (logger != null) logger.atFatal(e).withPrefix(LogPrefixes.REFLECTION).log("While setting field occur error");
            return false;
        }
    }

    private static void setFieldViaUnsafe(Field field, @Nullable Object instance, @Nullable Object value) throws UnsupportedOperationException {
        if (UNSAFE == null) {
            throw new UnsupportedOperationException("sun.misc.Unsafe is not available to modify final fields in this environment");
        }

        boolean isStatic = Modifier.isStatic(field.getModifiers());
        Object  base     = isStatic ? UNSAFE.staticFieldBase(field) : instance;
        long    offset   = isStatic ? UNSAFE.staticFieldOffset(field) : UNSAFE.objectFieldOffset(field);

        Class<?> type = field.getType();
        if (!type.isPrimitive()) UNSAFE.putObject(base, offset, value);
        else if (type == int.class) UNSAFE.putInt(base, offset, value != null ? (Integer) value : 0);
        else if (type == boolean.class) UNSAFE.putBoolean(base, offset, value != null ? (Boolean) value : false);
        else if (type == long.class) UNSAFE.putLong(base, offset, value != null ? (Long) value : 0L);
        else if (type == float.class) UNSAFE.putFloat(base, offset, value != null ? (Float) value : 0.0F);
        else if (type == double.class) UNSAFE.putDouble(base, offset, value != null ? (Double) value : 0.0);
        else if (type == byte.class) UNSAFE.putByte(base, offset, value != null ? (Byte) value : (byte) 0);
        else if (type == short.class) UNSAFE.putShort(base, offset, value != null ? (Short) value : (short) 0);
        else if (type == char.class) UNSAFE.putChar(base, offset, value != null ? (Character) value : '\0');
    }

    @SuppressWarnings("UnusedReturnValue")
    public static <T> boolean invokeMethod(
            Class<? extends T> clazz,
            String methodName, @Nullable T instance,
            @Nullable MouseLogger logger
    ) {
        try {
            Method method = clazz.getDeclaredMethod(methodName);
            method.trySetAccessible();
            method.invoke(instance);
            return true;
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            if (logger != null) logger.atFatal(e).withPrefix(LogPrefixes.REFLECTION).log("While invoke method error occurring");
            return false;
        }
    }

    public static <T, D> boolean invokeMethod(
            Class<? extends T> clazz, Class<D> parType,
            String methodName, @Nullable T instance, @Nullable D par,
            @Nullable MouseLogger logger
    ) {
        try {
            Method method = clazz.getDeclaredMethod(methodName, parType);
            method.trySetAccessible();
            method.invoke(instance, par);
            return true;
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            if (logger != null) logger.atFatal(e).withPrefix(LogPrefixes.REFLECTION).log("While invoke method error occurring");
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T, R> VariableValue<R> invokeMethod(
            Class<? extends T> clazz, Class<R> returnType,
            String methodName, @Nullable T instance,
            @Nullable MouseLogger logger
    ) {
        try {
            Method method = clazz.getDeclaredMethod(methodName);
            if (method.getReturnType() == returnType) {
                method.trySetAccessible();
                return VariableValue.create((R) method.invoke(instance));
            } else {
                throw new RuntimeException("Method '" + methodName + "' found, but that return type incompatible. Actual type '" +
                        method.getReturnType() + "'; Expected type '" + returnType + "'");
            }
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            if (logger != null) logger.atFatal(e).withPrefix(LogPrefixes.REFLECTION).log("While invoke method error occurring");
            return VariableValue.create();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T, D, R> VariableValue<R> invokeMethod(
            Class<? extends T> clazz, Class<D> parType, Class<R> returnType,
            String methodName, @Nullable T instance, @Nullable D par,
            @Nullable MouseLogger logger
    ) {
        try {
            Method method = clazz.getDeclaredMethod(methodName, parType);
            if (method.getReturnType() == returnType) {
                method.trySetAccessible();
                return VariableValue.create((R) method.invoke(instance, par));
            } else {
                throw new RuntimeException("Method '" + methodName + "' found, but that return type incompatible. Actual type '" +
                        method.getReturnType() + "'; Expected type '" + returnType + "'");
            }
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            if (logger != null) logger.atFatal(e).withPrefix(LogPrefixes.REFLECTION).log("While invoke method error occurring");
            return VariableValue.create();
        }
    }
}