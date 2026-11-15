/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype.values;

import ru.mousecray.mousecore.api.anno.MethodReturnsNonnullByDefault;
import ru.mousecray.mousecore.api.customtype.NumberType;
import ru.mousecray.mousecore.api.customtype.type.IValType;
import ru.mousecray.mousecore.api.customtype.type.QuantityDistribution;
import ru.mousecray.mousecore.api.customtype.type.ValType;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.utils.MouseStrings;
import ru.mousecray.mousecore.api.utils.MouseUtils;

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
        this.min = min.asInt();
        this.max = max.asInt();
    }

    public static RandomQuantityType create(PercentType chance, IntegralType min, IntegralType max) {
        if (chance.getLogicPipeline().isLessOrEqual(MIN.getChance()).asBoolean()
                && min.getLogicPipeline().isLessOrEqual(MIN.getMin()).asBoolean()
                && max.getLogicPipeline().isLessOrEqual(MIN.getMax()).asBoolean()
        ) return MIN;
        else if (chance.getLogicPipeline().isMoreOrEqual(MAX.getChance()).asBoolean()
                && min.getLogicPipeline().isMoreOrEqual(MIN.getMin()).asBoolean()
                && max.getLogicPipeline().isMoreOrEqual(MIN.getMax()).asBoolean()) return MAX;
        else if (chance.getLogicPipeline().isEqual(NULL.getChance()).asBoolean()
                && min.getLogicPipeline().isMoreOrEqual(MIN.getMin()).asBoolean()
                && max.getLogicPipeline().isMoreOrEqual(MIN.getMax()).asBoolean()) return NULL;
        else return new RandomQuantityType(chance, min, max);
    }

    @Override public IValType getValType() { return ValType.RANDOM_QUANTITY; }

    public static RandomQuantityType calcMedium(RandomQuantityType min, RandomQuantityType max) {
        return create(
                PercentType.create(min.value + max.value / 2D),
                IntegralType.create((long) (min.min + max.min / 2D)),
                IntegralType.create((long) (min.max + max.max / 2D))
        );
    }

    public static RandomQuantityType calcLow(RandomQuantityType min, RandomQuantityType max) {
        return create(
                PercentType.create((min.value + (min.value + max.value) / 2D) / 2D),
                IntegralType.create((long) (min.min + (min.min + max.min / 2D) / 2D)),
                IntegralType.create((long) (min.max + (min.max + max.max / 2D) / 2D))
        );
    }

    public static RandomQuantityType calcHigh(RandomQuantityType min, RandomQuantityType max) {
        return create(
                PercentType.create((max.value + (min.value + max.value) / 2D) / 2D),
                IntegralType.create((long) (max.min + (min.min + max.min / 2D) / 2D)),
                IntegralType.create((long) (max.max + (min.max + max.max / 2D) / 2D))
        );
    }

    public PercentType getChance()     { return PercentType.create(value); }
    public IntegralType getMin()       { return IntegralType.create(min); }
    public IntegralType getMax()       { return IntegralType.create(max); }

    @Override public Double asNumber() { return value; }

    public int roll(Random random)     { return roll(random, QuantityDistribution.CHANCE_OR_ZERO); }
    public int roll()                  { return roll(ThreadLocalRandom.current(), QuantityDistribution.CHANCE_OR_ZERO); }

    public int roll(Random random, QuantityDistribution distribution) {
        int low  = Math.min(min, max);
        int high = Math.max(min, max);
        if (low == high) return low;

        double p = Math.max(0.0, Math.min(100.0, getChance().asDouble())) / 100.0;

        switch (distribution) {
            case SMOOTH_BIAS: {
                if (p <= 0.0) return low;
                if (p >= 1.0) return high;

                double u      = random.nextDouble();
                double factor = Math.pow(u, (1.0 - p) / p);
                return low + (int) Math.round((high - low) * factor);
            }

            case PEAK_AT_PERCENT: {
                if (p <= 0.0) return low;
                if (p >= 1.0) return high;

                double u = random.nextDouble();
                double factor = (u < p)
                        ? Math.sqrt(u * p)
                        : 1.0 - Math.sqrt((1.0 - u) * (1.0 - p));
                return low + (int) Math.round((high - low) * factor);
            }

            case BELL_CLUSTER: {
                if (p <= 0.0) return low;
                if (p >= 1.0) return high;

                double target = low + (high - low) * p;
                double spread = (high - low) / 6.0;
                double value  = target + random.nextGaussian() * spread;
                return (int) Math.round(Math.max(low, Math.min(high, value)));
            }

            case CHANCE_OR_ZERO: {
                if (p <= 0.0) return 0;
                if (p < 1.0 && (random.nextDouble() > p)) return 0;
                return low + random.nextInt(high - low + 1);
            }

            case FLAT_RANDOM:
            default:
                return low + random.nextInt(high - low + 1);
        }
    }

    public int roll(QuantityDistribution distribution) { return roll(ThreadLocalRandom.current(), distribution); }

    @SuppressWarnings("unchecked") @Override
    public RandomQuantityType createType(Number value) { return create(PercentType.create(value.doubleValue()), IntegralType.NULL, IntegralType.NULL); }

    @Override
    public String toString() {
        String result = MouseStrings.format("{0}-{1}:{2}%", getMin().toString(), getMax().toString(), super.toString());
        assert result != null;
        return result;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass") @Override
    public boolean equals(Object obj) { return EQUATOR.test(this, obj); }

    @Override public int hashCode() { return Objects.hash(value, min, max); }
}