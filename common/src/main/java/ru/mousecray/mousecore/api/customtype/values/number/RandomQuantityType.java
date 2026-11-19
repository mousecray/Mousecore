/*
 * ******************************************************************************
 *  * Copyright © 2026 mousecray
 *  * Licensed under the GNU Lesser General Public License, Version 3.0
 *  *****************************************************************************
 */

package ru.mousecray.mousecore.api.customtype.values.number;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.NumberType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.utils.MouseStrings;
import ru.mousecray.mousecore.api.utils.MouseUtils;
import ru.mousecray.mousecore.api.utils.QuantityDistribution;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static ru.mousecray.mousecore.api.utils.MouseNumbers.tryParseDouble;
import static ru.mousecray.mousecore.api.utils.MouseStrings.subAndTrimWithTabs;
import static ru.mousecray.mousecore.api.utils.MouseStrings.trimWith;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class RandomQuantityType extends NumberType<Double> {
    private static final MouseUtils.Equator EQUATOR = MouseUtils.Equator
            .equaling(RandomQuantityType::getChance)
            .thenEqualing(RandomQuantityType::getMin)
            .thenEqualing(RandomQuantityType::getMax);

    public static final RandomQuantityType MIN  = new RandomQuantityType(PercentType.MIN, IntegralType.NULL, IntegralType.NULL);
    public static final RandomQuantityType MAX  = new RandomQuantityType(PercentType.MAX, IntegralType.MAX, IntegralType.MAX);
    public static final RandomQuantityType NULL = new RandomQuantityType(PercentType.NULL, IntegralType.NULL, IntegralType.NULL);
    
    static {
        storage.put(RandomQuantityType.class, str -> {
            str = MouseStrings.trimWith(str, true, '\t');
            if (str == null || str.isEmpty()) throw new ValueFormatException();

            Double min    = null, max = null;
            Double chance = null;

            String[] split = str.split(":");
            if (split.length > 0) {
                String[] split2 = split[0].split("-");
                if (split2.length > 0) min = tryParseDouble(trimWith(split2[0], true, '\t'));
                if (split2.length > 1) max = tryParseDouble(trimWith(split2[1], true, '\t'));

                if (split.length > 1) chance = tryParseDouble(subAndTrimWithTabs(split[1], null, "%"));
            }

            if (min == null) min = 0D;
            if (max == null) max = min;
            if (chance == null) throw new ValueFormatException();

            return create(
                    PercentType.create(chance),
                    IntegralType.create(min.longValue()),
                    IntegralType.create(max.longValue())
            );
        });
    }

    private final int min;
    private final int max;

    private RandomQuantityType(PercentType chance, IntegralType min, IntegralType max) {
        super(chance.asDouble());
        this.min = (int) Math.max(0L, Math.min(Integer.MAX_VALUE, min.asLong()));
        this.max = (int) Math.max(0L, Math.min(Integer.MAX_VALUE, max.asLong()));
    }

    public static RandomQuantityType create(PercentType chance, IntegralType min, IntegralType max) {
        double ch   = chance.asDouble();
        long   minV = Math.max(0L, Math.min(Integer.MAX_VALUE, min.asLong()));
        long   maxV = Math.max(0L, Math.min(Integer.MAX_VALUE, max.asLong()));

        if (ch <= 0D && minV == 0L && maxV == 0L) return NULL;
        if (ch >= 100D && minV >= Integer.MAX_VALUE && maxV >= Integer.MAX_VALUE) return MAX;
        return new RandomQuantityType(chance, IntegralType.create(minV), IntegralType.create(maxV));
    }

    @Override public IValType getValType() { return ValType.RANDOM_QUANTITY; }

    public static RandomQuantityType calcMedium(RandomQuantityType min, RandomQuantityType max) {
        return create(
                PercentType.create(min.value * 0.5D + max.value * 0.5D),
                IntegralType.create((long) (min.min * 0.5D + max.min * 0.5D)),
                IntegralType.create((long) (min.max * 0.5D + max.max * 0.5D))
        );
    }

    public static RandomQuantityType calcLow(RandomQuantityType min, RandomQuantityType max) {
        return create(
                PercentType.create(min.value * 0.75D + max.value * 0.25D),
                IntegralType.create((long) (min.min * 0.75D + max.min * 0.25D)),
                IntegralType.create((long) (min.max * 0.75D + max.max * 0.25D))
        );
    }

    public static RandomQuantityType calcHigh(RandomQuantityType min, RandomQuantityType max) {
        return create(
                PercentType.create(min.value * 0.25D + max.value * 0.75D),
                IntegralType.create((long) (min.min * 0.25D + max.min * 0.75D)),
                IntegralType.create((long) (min.max * 0.25D + max.max * 0.75D))
        );
    }

    public PercentType getChance()     { return PercentType.create(value); }
    public IntegralType getMin()       { return IntegralType.create(min); }
    public IntegralType getMax()       { return IntegralType.create(max); }

    @Override public Double asNumber() { return value; }

    public int roll(Random random, QuantityDistribution distribution) {
        return distribution.rollInt(random, min, max, value);
    }

    public int roll(Random random) {
        return roll(random, QuantityDistribution.CHANCE_OR_ZERO);
    }

    public int roll(QuantityDistribution distribution) {
        return roll(ThreadLocalRandom.current(), distribution);
    }

    public int roll() {
        return roll(ThreadLocalRandom.current(), QuantityDistribution.CHANCE_OR_ZERO);
    }

    @SuppressWarnings("unchecked")
    @Override public RandomQuantityType createType(Number value) {
        return create(PercentType.create(value.doubleValue()), IntegralType.NULL, IntegralType.NULL);
    }

    @Override
    public String toString() {
        String result = MouseStrings.format("{0}-{1}:{2}%", getMin().toString(), getMax().toString(), super.toString());
        assert result != null;
        return result;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override public boolean equals(Object obj) { return EQUATOR.test(this, obj); }

    @Override public int hashCode() { return Objects.hash(value, min, max); }
}