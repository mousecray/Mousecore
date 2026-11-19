/*******************************************************************************
 *  Copyright © 2026 mousecray
 *  Licensed under the GNU Lesser General Public License, Version 3.0
 ****************************************************************************** */

package ru.mousecray.mousecore.api.customtype.values.number;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.NumberType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.utils.MouseNumbers;
import ru.mousecray.mousecore.api.utils.MouseStrings;
import ru.mousecray.mousecore.api.utils.MouseUtils;
import ru.mousecray.mousecore.api.utils.QuantityDistribution;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Comparator;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public class RangeType extends NumberType<Double> {
    private static final MouseUtils.Equator<RangeType> EQUATOR = MouseUtils.Equator
            .equaling(RangeType::getMinDouble)
            .thenEqualing(RangeType::getMaxDouble)
            .thenEqualing(RangeType::isIncludeMin)
            .thenEqualing(RangeType::isIncludeMax);

    public static final RangeType ZERO = new RangeType(0D, 0D, true, true);

    static {
        storage.put(RangeType.class, RangeType::parse);
    }

    protected final double  min;
    protected final double  max;
    protected final boolean includeMin;
    protected final boolean includeMax;

    protected RangeType(double min, double max, boolean includeMin, boolean includeMax) {
        super(max - min);
        if (min > max) {
            throw new ValueFormatException("Range minimum (" + min + ") cannot be greater than maximum (" + max + ")");
        }
        this.min = min;
        this.max = max;
        this.includeMin = includeMin;
        this.includeMax = includeMax;
    }

    public static RangeType create(double min, double max) {
        return new RangeType(min, max, true, true);
    }

    public static RangeType create(double min, double max, boolean includeMin, boolean includeMax) {
        return new RangeType(min, max, includeMin, includeMax);
    }

    public static RangeType create(Number min, Number max) {
        return new RangeType(min.doubleValue(), max.doubleValue(), true, true);
    }

    public static RangeType create(Number min, Number max, boolean includeMin, boolean includeMax) {
        return new RangeType(min.doubleValue(), max.doubleValue(), includeMin, includeMax);
    }

    public static RangeType parse(@Nullable String raw) {
        raw = MouseStrings.trimWith(raw, true, '\t');
        if (raw == null || raw.isEmpty()) throw new ValueFormatException("Cannot parse empty string as Range");

        boolean incMin = true;
        boolean incMax = true;

        if (raw.startsWith("[")) raw = raw.substring(1);
        else if (raw.startsWith("(")) {
            incMin = false;
            raw = raw.substring(1);
        }

        if (raw.endsWith("]")) {
            raw = raw.substring(0, raw.length() - 1);
        } else if (raw.endsWith(")")) {
            incMax = false;
            raw = raw.substring(0, raw.length() - 1);
        }

        String   delimiter = raw.contains(";") ? ";" : (raw.contains("..") ? "\\.\\." : ",");
        String[] parts     = raw.split(delimiter, 2);
        if (parts.length < 2) {
            throw new ValueFormatException("Range string must contain a separator (';', '..' or ','): " + raw);
        }

        Double minVal = MouseNumbers.tryParseDouble(MouseStrings.trimWith(parts[0], true, '\t'));
        Double maxVal = MouseNumbers.tryParseDouble(MouseStrings.trimWith(parts[1], true, '\t'));

        if (minVal == null || maxVal == null) {
            throw new ValueFormatException("Cannot parse range boundaries: " + raw);
        }

        return create(minVal, maxVal, incMin, incMax);
    }

    public DecimalType getMin()   { return DecimalType.create(min); }
    public DecimalType getMax()   { return DecimalType.create(max); }
    public double getMinDouble()  { return min; }
    public double getMaxDouble()  { return max; }
    public long getMinLong()      { return (long) min; }
    public long getMaxLong()      { return (long) max; }
    public boolean isIncludeMin() { return includeMin; }
    public boolean isIncludeMax() { return includeMax; }
    public double getSpan()       { return value; }

    public boolean isInRange(double val) {
        boolean minOk = includeMin ? val >= min : val > min;
        boolean maxOk = includeMax ? val <= max : val < max;
        return minOk && maxOk;
    }

    public boolean isInRange(Number val) {
        return isInRange(val.doubleValue());
    }

    public boolean isInRange(NumberType<?> val) {
        return isInRange(val.asDouble());
    }

    public double rollDouble() {
        return rollDouble(ThreadLocalRandom.current(), QuantityDistribution.FLAT_RANDOM, 50.0);
    }

    public double rollDouble(QuantityDistribution distribution) {
        return rollDouble(ThreadLocalRandom.current(), distribution, 50.0);
    }

    public double rollDouble(Random random, QuantityDistribution distribution) {
        return rollDouble(random, distribution, 50.0);
    }

    public double rollDouble(Random random, QuantityDistribution distribution, double biasPercent) {
        return distribution.roll(random, min, max, biasPercent);
    }

    public long rollLong() {
        return rollLong(ThreadLocalRandom.current(), QuantityDistribution.FLAT_RANDOM, 50.0);
    }

    public long rollLong(QuantityDistribution distribution) {
        return rollLong(ThreadLocalRandom.current(), distribution, 50.0);
    }

    public long rollLong(Random random, QuantityDistribution distribution) {
        return rollLong(random, distribution, 50.0);
    }

    public long rollLong(Random random, QuantityDistribution distribution, double biasPercent) {
        return distribution.rollLong(random, (long) min, (long) max, biasPercent);
    }

    @Override public Double asNumber() { return value; }

    @SuppressWarnings("unchecked")
    @Override public RangeType createType(Number value) { return create(0D, value.doubleValue()); }

    @Override public IValType getValType() { return ValType.RANGE; }

    @Override
    public String toString() {
        return (includeMin ? '[' : '(')
                + MouseNumbers.formatDecimal(min, false, true)
                + "; "
                + MouseNumbers.formatDecimal(max, false, true)
                + (includeMax ? ']' : ')');
    }

    @Override public int hashCode() { return Objects.hash(min, max, includeMin, includeMax); }

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object obj) { return EQUATOR.test(this, obj); }

    @Override
    public int compareTo(NumberType<?> o) {
        if (o instanceof RangeType other) {
            return Comparator
                    .comparingDouble(RangeType::getMinDouble)
                    .thenComparingDouble(RangeType::getMaxDouble)
                    .thenComparing(RangeType::isIncludeMin)
                    .thenComparing(RangeType::isIncludeMax)
                    .compare(this, other);
        }
        return super.compareTo(o);
    }
}