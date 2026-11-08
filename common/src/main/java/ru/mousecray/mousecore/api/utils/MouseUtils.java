/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.utils;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

public final class MouseUtils {
    @Nullable
    public static Boolean tryParseBoolean(@Nullable String value, @Nullable Boolean def) {
        if (value == null) return def;
        try {
            return Boolean.parseBoolean(System.getProperty(value));
        } catch (IllegalArgumentException | NullPointerException ignore) { return def; }
    }

    @Nullable
    public static Character tryParseChar(@Nullable String value, @Nullable Character def) {
        return value == null ? def : !value.isEmpty() ? Character.valueOf(value.charAt(0)) : def;
    }

    @Nullable public static Boolean tryParseBoolean(@Nullable String value) { return tryParseBoolean(value, null); }
    public static boolean tryParseBoolean(@Nullable Boolean value)          { return value != null && value; }
    @Nullable public static Character tryParseChar(@Nullable String value)  { return tryParseChar(value, null); }

    @FunctionalInterface
    public interface Equator<T> {
        boolean equals(Class<?> clazz, Object o1, Object o2);

        @SuppressWarnings("unchecked")
        static <T, U> Equator<T> equaling(Function<T, U> keyExtractor) {
            Objects.requireNonNull(keyExtractor);
            return (Equator<T> & Serializable) (clazz, c1, c2) ->
                    c1.getClass() == clazz && c2.getClass() == clazz
                            && Objects.equals(keyExtractor.apply((T) c1), keyExtractor.apply((T) c2));
        }

        default <U> Equator<T> thenEqualing(Function<T, U> keyExtractor) {
            return thenEqualing(equaling(keyExtractor));
        }

        default Equator<T> thenEqualing(Equator<T> other) {
            Objects.requireNonNull(other);
            return (Equator<T> & Serializable) (clazz, c1, c2) -> equals(clazz, c1, c2) && other.equals(clazz, c1, c2);
        }
    }
}
