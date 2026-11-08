/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.config.utils;

import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public final class PredefinedValue<T> {
    private final String displayName;
    private final T      value;

    @SuppressWarnings("DataFlowIssue")
    public PredefinedValue(String displayName, T value) {
        Objects.requireNonNull(displayName);
        displayName = MouseStrings.trimWith(displayName, true, '\t');
        if (displayName.isEmpty()) throw new IllegalArgumentException("displayName cannot be empty");
        if (displayName.contains("\t")) throw new IllegalArgumentException("displayName cannot contains tabs");
        this.displayName = displayName;
        this.value = value;
    }

    @Override @Nonnull
    public String toString() { return "PredefinedValue{displayName='" + displayName + '\'' + ", value=" + value + '}'; }
    @Override public int hashCode() { return Objects.hash(displayName, value); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PredefinedValue<?> that = (PredefinedValue<?>) o;
        return displayName.equals(that.displayName) && Objects.equals(value, that.value);
    }

    public String getDisplayName() { return displayName; }
    public T getValue()            { return value; }
}