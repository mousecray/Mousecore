/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.utils;

import ru.mousecray.mousecore.api.VariableValue;
import ru.mousecray.mousecore.api.log.MouseLogger;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;


public class MouseReflection {
    private final           Class<?>    clazz;
    @Nullable private final Field       modifiers;
    @Nullable private final MouseLogger logger;

    private MouseReflection(Class<?> clazz, @Nullable Field modifiers, @Nullable MouseLogger logger) {
        this.clazz = clazz;
        this.modifiers = modifiers;
        this.logger = logger;
    }

    public static MouseReflection prepare(Class<?> clazz, @Nullable MouseLogger logger) { return new MouseReflection(clazz, null, logger); }
    public static MouseReflection prepare(Class<?> clazz)                               { return prepare(clazz, null); }

    public static MouseReflection prepareForFinal(Class<?> clazz, @Nullable MouseLogger logger) {
        Field modifiersField;
        try {
            modifiersField = Field.class.getDeclaredField("modifiers");
        } catch (NoSuchFieldException e) {
            if (logger != null) logger.atFatal(e).withPrefix("Reflection").log("Cannot find modifiers field");
            throw new RuntimeException(e);
        }
        modifiersField.setAccessible(true);
        return new MouseReflection(clazz, modifiersField, logger);
    }

    public static MouseReflection prepareForFinal(Class<?> clazz) { return prepareForFinal(clazz, null); }


    @SuppressWarnings("unchecked")
    public <T> List<T> getPublicStaticFields() {
        List<T> result = new ArrayList<>();
        for (Field field : clazz.getFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);
                try { result.add((T) field.get(null)); } catch (IllegalAccessException e) {
                    if (logger != null) logger.atFatal(e).withPrefix("Reflection").log("While getting fields an error occurring");
                    throw new RuntimeException(e);
                }
            }
        }
        return result;
    }

    public static <T, D> boolean setField(
            Class<? extends T> clazz, Class<D> fieldType,
            String fieldName, @Nullable T instance, @Nullable D value,
            @Nullable MouseLogger logger
    ) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            if (field.getType() == fieldType) {
                boolean isAccessible = field.isAccessible();
                if (!isAccessible) field.setAccessible(true);

                Field   modifiersField  = Field.class.getDeclaredField("modifiers");
                boolean isAccessibleMod = modifiersField.isAccessible();
                if (!isAccessibleMod) modifiersField.setAccessible(true);

                int     modifiers = field.getModifiers();
                boolean isFinal   = Modifier.isFinal(modifiers);
                if (isFinal) modifiersField.setInt(field, modifiers & ~Modifier.FINAL);

                field.set(instance, value);

                if (isFinal) modifiersField.setInt(field, modifiers & Modifier.FINAL);

                if (!isAccessibleMod) modifiersField.setAccessible(false);

                if (!isAccessible) field.setAccessible(false);
                return true;
            } else {
                throw new RuntimeException("Field '" + fieldName + "' found, but that type incompatible. Actual type '" +
                        field.getType() + "'; Expected type '" + fieldType + "'");
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            if (logger != null) logger.atFatal(e).withPrefix("Reflection").log("While setting field occur error");
            return false;
        }
    }

    public static <T> boolean invokeMethod(
            Class<? extends T> clazz,
            String methodName, @Nullable T instance,
            @Nullable MouseLogger logger
    ) {
        try {
            Method  method       = clazz.getDeclaredMethod(methodName);
            boolean isAccessible = method.isAccessible();
            if (!isAccessible) method.setAccessible(true);

            method.invoke(instance);

            if (!isAccessible) method.setAccessible(false);
            return true;
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            if (logger != null) logger.atFatal(e).withPrefix("Reflection").log("While invoke method error occurring");
            return false;
        }
    }

    public static <T, D> boolean invokeMethod(
            Class<? extends T> clazz, Class<D> parType,
            String methodName, @Nullable T instance, @Nullable D par,
            @Nullable MouseLogger logger
    ) {
        try {
            Method  method       = clazz.getDeclaredMethod(methodName, parType);
            boolean isAccessible = method.isAccessible();
            if (!isAccessible) method.setAccessible(true);

            method.invoke(instance, par);

            if (!isAccessible) method.setAccessible(false);
            return true;
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            if (logger != null) logger.atFatal(e).withPrefix("Reflection").log("While invoke method error occurring");
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
                boolean isAccessible = method.isAccessible();
                if (!isAccessible) method.setAccessible(true);

                VariableValue<R> var = VariableValue.create(((R) method.invoke(instance)));

                if (!isAccessible) method.setAccessible(false);

                return var;
            } else {
                throw new RuntimeException("Method '" + methodName + "' found, but that return type incompatible. Actual type '" +
                        method.getReturnType() + "'; Expected type '" + returnType + "'");
            }
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            if (logger != null) logger.atFatal(e).withPrefix("Reflection").log("While invoke method error occurring");
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
                boolean isAccessible = method.isAccessible();
                if (!isAccessible) method.setAccessible(true);

                VariableValue<R> var = VariableValue.create(((R) method.invoke(instance, par)));

                if (!isAccessible) method.setAccessible(false);
                return var;
            } else {
                throw new RuntimeException("Method '" + methodName + "' found, but that return type incompatible. Actual type '" +
                        method.getReturnType() + "'; Expected type '" + returnType + "'");
            }
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            if (logger != null) logger.atFatal(e).withPrefix("Reflection").log("While invoke method error occurring");
            return VariableValue.create();
        }
    }
}
