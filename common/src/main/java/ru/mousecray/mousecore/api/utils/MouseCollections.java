/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.utils;

import ru.mousecray.mousecore.api.anno.Fast;
import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.anno.Slowly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class MouseCollections {
    @SafeVarargs
    public static <T> boolean hasAny(T target, T... array) {
        for (T t : array) if (target == t) return true;
        return false;
    }

    private static boolean hasAny(char target, char... array) {
        for (char c : array) if (target == c) return true;
        return false;
    }

    @Nullable @SuppressWarnings("unchecked")
    public static <T> T[] map(@Nullable Function<T, T> mapper, boolean removeNulls, @Nullable T... array) {
        if (array == null) return null;
        if (mapper == null) return array;

        return Arrays.stream(array).filter(val -> !removeNulls || val != null).map(mapper)
                .toArray(i -> (T[]) Array.newInstance(array.getClass().getComponentType(), i));
    }

    @SafeVarargs @Nonnull
    public static <T, D> List<D> mapAsList(@Nullable Function<T, D> mapper, boolean removeNulls, @Nullable T... array) {
        if (array == null || mapper == null) return new ArrayList<>();

        return Arrays.stream(array)
                .filter(val -> !removeNulls || val != null)
                .map(mapper)
                .collect(Collectors.toList());
    }

    @Fast @Nonnull
    public static <T, D> List<D> map(@Nullable Function<T, D> mapper, boolean removeNulls, @Nullable Collection<T> input) {
        if (input == null || mapper == null) return new ArrayList<>();

        List<D> result = new ArrayList<>();
        for (T val : input) if (!removeNulls || val != null) result.add(mapper.apply(val));
        return result;
    }

    /**
     * Adds list to array. Order not granted
     */
    @SuppressWarnings("unchecked") @Slowly
    public static <T> T[] addAll(@Nonnull Class<? super T> componentType, @Nullable T[] array, @Nullable Collection<T> collection) {
        Objects.requireNonNull(componentType);
        if (array == null && collection != null) return collection.toArray((T[]) Array.newInstance(componentType, 0));
        else if (array != null && collection == null) return array;
        else if (array != null) {
            List<T> list = new ArrayList<>(collection);
            Collections.addAll(list, array);
            return list.toArray((T[]) Array.newInstance(componentType, 0));
        } else return (T[]) Array.newInstance(componentType, 0);
    }

    @SuppressWarnings("unchecked") @Nullable
    public static <T> T[] toArray(@Nullable Collection<T> collection, Class<? super T> componentType) {
        return collection == null ? null : collection.toArray((T[]) Array.newInstance(componentType, 0));
    }

    public static <T> boolean ifAny(@Nullable T[] array, @Nullable Predicate<T> ifs) {
        if (array == null) return false;
        for (T t : array) if (ifs == null || ifs.test(t)) return true;
        return false;
    }

    public static <T> boolean ifAny(@Nullable Collection<T> collection, @Nullable Predicate<T> ifs) {
        if (collection == null) return false;
        for (T t : collection) if (ifs == null || ifs.test(t)) return true;
        return false;
    }

    @Nullable
    public static <T> T findAny(@Nullable Collection<T> collection, @Nullable Predicate<? super T> ifs) {
        if (collection == null) return null;
        for (T t : collection) if (ifs == null || ifs.test(t)) return t;
        return null;
    }

    public static <T, D> Map<T, D> createMap(T key, D value) {
        Map<T, D> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    public static <T, D> Map<T, D> createMap(T key, D value, T key1, D value1) {
        Map<T, D> map = new HashMap<>();
        map.put(key, value);
        map.put(key1, value1);
        return map;
    }

    public static <T extends Comparable<? super T>> int compare(@Nullable T[] a, @Nullable T[] b) {
        if (a == b) return 0;
        else if (a != null && b != null) {
            int length = Math.min(a.length, b.length);
            for (int i = 0; i < length; ++i) {
                T oa = a[i], ob = b[i];
                if (oa != ob) {
                    if (oa == null || ob == null) return oa == null ? -1 : 1;
                    int v = oa.compareTo(ob);
                    if (v != 0) return v;
                }
            }
            return a.length - b.length;
        } else return a == null ? -1 : 1;
    }
}
