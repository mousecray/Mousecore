/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

public final class VariableValue<T> {
    private final T       value;
    private final boolean present;

    private VariableValue(T value, boolean present) {
        this.value = value;
        this.present = present;
    }

    @Nonnull
    public static <T> VariableValue<T> create(@Nullable T value) { return new VariableValue<>(value, true); }

    @Nonnull
    public static <T> VariableValue<T> create() { return new VariableValue<>(null, false); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VariableValue<?> that = (VariableValue<?>) o;
        return present == that.present && Objects.equals(value, that.value);
    }

    @Override public int hashCode() { return Objects.hash(value, present); }

    @Override @Nonnull
    public String toString() {
        return "VariableValue{value=" + value + ", present=" + present + '}';
    }

    public boolean isPresent() { return present; }

    public boolean ifPresent(@Nonnull Consumer<T> func) {
        if (present) {
            func.accept(value);
            return true;
        } else return false;
    }

    public boolean ifNotPresent(@Nonnull VoidConsumer func) {
        if (!present) {
            func.accept();
            return true;
        } else return false;
    }

    public T getValue() {
        if (!present) throw new IllegalStateException("Value not present");
        return value;
    }

    public T getOrDefault(T def) { return !present ? def : value; }
}