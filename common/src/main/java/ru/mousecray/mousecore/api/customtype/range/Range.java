/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype.range;

import ru.mousecray.mousecore.api.customtype.NumberType;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.Objects;

public class Range<T extends NumberType<?>> implements Comparable<Range<T>> {
    private final T minValue, maxValue;
    private final boolean includeMin;
    private final boolean includeMax;

    private Range(@Nonnull T minValue, @Nonnull T maxValue, boolean includeMin, boolean includeMax) {
        Objects.requireNonNull(minValue);
        Objects.requireNonNull(maxValue);
        this.minValue = minValue;
        this.includeMin = includeMin;
        this.includeMax = includeMax;
        this.maxValue = maxValue;
    }

    public static <T extends NumberType<?>> Range<T> of(T min, T max) {
        return new Range<>(min, max, true, true);
    }

    public static <T extends NumberType<?>> Range<T> of(T min, T max, boolean includeMin, boolean includeMax) {
        return new Range<>(min, max, includeMin, includeMax);
    }

    public T getMinValue()        { return minValue; }
    public T getMaxValue()        { return maxValue; }
    public boolean isIncludeMin() { return includeMin; }
    public boolean isIncludeMax() { return includeMax; }

    public boolean isLess(T value) {
        return includeMin ? value.getLogicPipeline().isLessOrEqual(minValue) : value.getLogicPipeline().isLess(minValue);
    }

    public boolean isMore(T value) {
        return includeMax ? value.getLogicPipeline().isMoreOrEqual(maxValue) : value.getLogicPipeline().isMore(maxValue);
    }

    public boolean isInRange(T value) {
        return includeMin
                ? minValue.getLogicPipeline().isLessOrEqual(value)
                : minValue.getLogicPipeline().isLess(value)
                && includeMax
                ? maxValue.getLogicPipeline().isMoreOrEqual(value)
                : maxValue.getLogicPipeline().isMore(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Range)) return false;
        Range<?> range = (Range<?>) o;
        return Objects.equals(minValue, range.minValue) && Objects.equals(maxValue, range.maxValue);
    }

    @Override public int hashCode() { return Objects.hash(minValue, maxValue); }

    @Override
    public int compareTo(@Nonnull Range<T> other) {
        return Comparator
                .<Range<T>, T>comparing(Range::getMinValue)
                .thenComparing(Range::getMaxValue)
                .compare(this, other);
    }

    @SuppressWarnings("StringBufferReplaceableByString") @Override
    public String toString() {
        return new StringBuilder().append("Range")
                .append(includeMin ? '[' : '(')
                .append(minValue).append(';')
                .append(maxValue)
                .append(includeMax ? ']' : ')')
                .toString();
    }

    @SuppressWarnings("unchecked")
    public static <T extends NumberType<?>> T distanceBetween(T first, T second) {
        return (T) second.getArithmeticPipeline().minus(first);
    }
}