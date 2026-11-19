/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.utils;

import ru.mousecray.mousecore.api.customtype.values.number.PercentType;
import ru.mousecray.mousecore.api.customtype.values.number.RandomQuantityType;
import ru.mousecray.mousecore.api.customtype.values.number.RangeType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@ParametersAreNonnullByDefault
public final class MouseRandom {

    private MouseRandom() { throw new UnsupportedOperationException("Cannot instantiate utility class MouseRandom"); }

    public static boolean chance(Random random, double percent) {
        if (percent <= 0.0) return false;
        if (percent >= 100.0) return true;
        return random.nextDouble() * 100.0 < percent;
    }

    public static boolean chance(double percent)                     { return chance(ThreadLocalRandom.current(), percent); }
    public static boolean chance(Random random, PercentType percent) { return chance(random, percent.asDouble()); }
    public static boolean chance(PercentType percent)                { return chance(ThreadLocalRandom.current(), percent.asDouble()); }

    public static double deviation(Random random, double baseValue, double maxDeviationPercent) {
        if (maxDeviationPercent <= 0.0) return baseValue;
        double spread = (random.nextDouble() * 2.0 - 1.0) * (maxDeviationPercent / 100.0);
        return baseValue * (1.0 + spread);
    }

    public static double deviation(double baseValue, double maxDeviationPercent) {
        return deviation(ThreadLocalRandom.current(), baseValue, maxDeviationPercent);
    }

    public static long deviationLong(Random random, long baseValue, double maxDeviationPercent) {
        return Math.round(deviation(random, (double) baseValue, maxDeviationPercent));
    }

    public static long deviationLong(long baseValue, double maxDeviationPercent) {
        return deviationLong(ThreadLocalRandom.current(), baseValue, maxDeviationPercent);
    }

    public static double roll(Random random, double min, double max, double biasPercent, QuantityDistribution distribution) {
        return distribution.roll(random, min, max, biasPercent);
    }

    public static double roll(double min, double max, double biasPercent, QuantityDistribution distribution) {
        return roll(ThreadLocalRandom.current(), min, max, biasPercent, distribution);
    }

    public static double roll(double min, double max) {
        return roll(ThreadLocalRandom.current(), min, max, 50.0, QuantityDistribution.FLAT_RANDOM);
    }

    public static int rollInt(Random random, int min, int max, double biasPercent, QuantityDistribution distribution) {
        return distribution.rollInt(random, min, max, biasPercent);
    }

    public static int rollInt(int min, int max, double biasPercent, QuantityDistribution distribution) {
        return rollInt(ThreadLocalRandom.current(), min, max, biasPercent, distribution);
    }

    public static int rollInt(int min, int max) {
        return rollInt(ThreadLocalRandom.current(), min, max, 50.0, QuantityDistribution.FLAT_RANDOM);
    }

    public static long rollLong(Random random, long min, long max, double biasPercent, QuantityDistribution distribution) {
        return distribution.rollLong(random, min, max, biasPercent);
    }

    public static long rollLong(long min, long max, double biasPercent, QuantityDistribution distribution) {
        return rollLong(ThreadLocalRandom.current(), min, max, biasPercent, distribution);
    }

    public static long rollLong(long min, long max) {
        return rollLong(ThreadLocalRandom.current(), min, max, 50.0, QuantityDistribution.FLAT_RANDOM);
    }

    public static int roll(RandomQuantityType quantity, Random random, QuantityDistribution distribution) { return quantity.roll(random, distribution); }
    public static int roll(RandomQuantityType quantity, QuantityDistribution distribution)                { return quantity.roll(distribution); }
    public static int roll(RandomQuantityType quantity)                                                   { return quantity.roll(); }
    public static double roll(RangeType range)                                                            { return range.rollDouble(); }

    public static double roll(RangeType range, Random random, QuantityDistribution distribution, double biasPercent) {
        return range.rollDouble(random, distribution, biasPercent);
    }
}