/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.utils;

import javax.annotation.Nullable;
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

    public static final class Equator<T> {
        private final Function<T, ?>[] extractors;

        @SafeVarargs
        private Equator(Function<T, ?>... extractors) {
            this.extractors = extractors;
        }

        public static <T> Equator<T> equaling(Function<T, ?> keyExtractor) {
            Objects.requireNonNull(keyExtractor);
            Function<T, ?>[] array = new Function[]{ keyExtractor };
            return new Equator<>(array);
        }

        public Equator<T> thenEqualing(Function<T, ?> keyExtractor) {
            Objects.requireNonNull(keyExtractor);
            Function<T, ?>[] newArray = new Function[extractors.length + 1];
            System.arraycopy(extractors, 0, newArray, 0, extractors.length);
            newArray[extractors.length] = keyExtractor;
            return new Equator<>(newArray);
        }

        @SuppressWarnings("unchecked")
        public boolean test(@Nullable Object o1, @Nullable Object o2) {
            if (o1 == o2) return true;
            if (o1 == null || o2 == null) return false;
            if (o1.getClass() != o2.getClass()) return false;

            T first  = (T) o1;
            T second = (T) o2;

            for (Function<T, ?> extractor : extractors) if (!Objects.equals(extractor.apply(first), extractor.apply(second))) return false;
            return true;
        }

        @SuppressWarnings("unchecked")
        public boolean equals(Class<?> clazz, @Nullable Object o1, @Nullable Object o2) {
            if (o1 == o2) return true;
            if (o1 == null || o2 == null) return false;
            if (o1.getClass() != clazz || o2.getClass() != clazz) return false;

            T first  = (T) o1;
            T second = (T) o2;

            for (Function<T, ?> extractor : extractors) if (!Objects.equals(extractor.apply(first), extractor.apply(second))) return false;
            return true;
        }
    }
}
