/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype.range;

import com.google.common.collect.ImmutableList;
import ru.mousecray.mousecore.api.customtype.NumberType;
import ru.mousecray.mousecore.api.utils.MouseCollections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class RangeContainer<T extends NumberType<?>> implements Comparable<RangeContainer<T>> {
    private final Range<T>[] ranges;
    private final T          minValue;
    private final T          maxValue;

    @SuppressWarnings("unchecked")
    public RangeContainer(Range<T>... ranges) {
        if (ranges == null) ranges = (Range<T>[]) Array.newInstance(Range.class, 0);

        ranges = Arrays.stream(ranges).filter(Objects::nonNull).toArray(Range[]::new);

        if (ranges.length == 0) {
            this.ranges = ranges;
            minValue = null;
            maxValue = null;
            return;
        }

        if (ranges.length == 1) this.ranges = ranges;
        else this.ranges = mergeOverlap(ranges);

        minValue = getMinFromRanges(this.ranges);
        maxValue = getMaxFromRanges(this.ranges);
    }

    @SuppressWarnings("unchecked")
    public RangeContainer(Collection<Range<T>> ranges) {
        if (ranges == null) ranges = new ArrayList<>();

        ranges = ranges.stream().filter(Objects::nonNull).collect(Collectors.toList());

        if (ranges.isEmpty()) {
            this.ranges = ranges.toArray((Range<T>[]) Array.newInstance(Range.class, 0));
            minValue = null;
            maxValue = null;
            return;
        }

        if (ranges.size() == 1) this.ranges = ranges.toArray(
                (Range<T>[]) Array.newInstance(Range.class, 0)
        );
        else this.ranges = mergeOverlap(ranges.toArray(
                (Range<T>[]) Array.newInstance(Range.class, 0))
        );

        minValue = getMinFromRanges(this.ranges);
        maxValue = getMaxFromRanges(this.ranges);
    }

    public boolean isEmpty() { return ranges.length == 0; }
    public T getMinValue()   { return minValue; }
    public T getMaxValue()   { return maxValue; }

    public boolean isInRange(T other) {
        return isEmpty() || MouseCollections.ifAny(ranges, p -> p.isInRange(other));
    }

//    public boolean isMoreMinExclude(T other) {
//        if (isEmpty()) return true;
//        return MouseCollections.ifAny(ranges, p -> p.isMoreMinExclude(other));
//    }
//
//    public boolean isLessMaxExclude(T other) {
//        if (isEmpty()) return true;
//        return MouseCollections.ifAny(ranges, p -> p.isLessMaxExclude(other));
//    }

    public T getNearValue(T other) {
        if (other == null || isEmpty()) return other;

        T distance = null;
        T prevMax  = null;
        for (Range<T> range : ranges) {
            if (range.isLess(other)) {
                if (prevMax != null) {
                    return Range.distanceBetween(other, minValue).compareTo(prevMax) < 0 ? minValue : prevMax;
                } else return minValue;
            } else if (range.isMore(other)) {
                prevMax = maxValue;
                distance = Range.distanceBetween(other, maxValue);
            }
        }

        return distance;
    }

    @Nonnull public ImmutableList<Range<T>> getRanges() { return ImmutableList.copyOf(ranges); }

    @SuppressWarnings("unchecked")
    private static <T extends NumberType<?>> Range<T>[] mergeOverlap(Range<T>[] array) {
        Arrays.sort(array, Comparator.comparing(Range::getMinValue));

        List<Range<T>> result = new ArrayList<>();
        result.add(array[0]);

        for (int i = 1; i < array.length; i++) {
            Range<T> last = result.get(result.size() - 1);
            Range<T> curr = array[i];


            if (curr.getMinValue().getLogicPipeline().isMoreOrEqual(last.getMaxValue()))
                result.set(result.size() - 1, Range.of(last.getMinValue(), getMaxFromRanges(last, curr)));
            else
                result.add(Range.of(curr.getMinValue(), curr.getMaxValue()));
        }

        return result.toArray((Range<T>[]) Array.newInstance(Range.class, 0));
    }

    @SafeVarargs @Nullable
    public static <T extends NumberType<?>> T getMinFromRanges(@Nullable Range<T>... ranges) {
        if (ranges == null) return null;
        T currentMin = null;
        for (Range<T> range : ranges) {
            if (currentMin == null || currentMin.getLogicPipeline().isMoreOrEqual(range.getMinValue())) {
                currentMin = range.getMinValue();
            }
        }
        return currentMin;
    }

    @SafeVarargs @Nullable
    public static <T extends NumberType<?>> T getMaxFromRanges(@Nullable Range<T>... ranges) {
        if (ranges == null) return null;
        T currentMax = null;
        for (Range<T> range : ranges) {
            if (currentMax == null || currentMax.getLogicPipeline().isLessOrEqual(range.getMaxValue())) {
                currentMax = range.getMaxValue();
            }
        }
        return currentMax;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RangeContainer)) return false;
        RangeContainer<?> that = (RangeContainer<?>) o;
        return Objects.deepEquals(ranges, that.ranges)
                && Objects.equals(minValue, that.minValue)
                && Objects.equals(maxValue, that.maxValue);
    }

    @Override public int hashCode() { return Objects.hash(Arrays.hashCode(ranges), minValue, maxValue); }

    @SuppressWarnings("StringBufferReplaceableByString") @Override
    public String toString() {
        return new StringBuilder().append("RangeContainer{ranges=")
                .append(Arrays.toString(ranges))
                .append(", minValue=").append(minValue)
                .append(", maxValue=").append(maxValue)
                .append('}').toString();
    }

    @Override
    public int compareTo(RangeContainer<T> other) {
        int result = MouseCollections.compare(ranges, other.ranges);
        if (result == 0) result = minValue.compareTo(other.minValue);
        if (result == 0) result = maxValue.compareTo(other.maxValue);
        return result;
    }
}