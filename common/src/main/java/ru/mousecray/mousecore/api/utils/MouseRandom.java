/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.utils;

import java.util.Random;

public final class MouseRandom {
    public static boolean normalChance(Random random, int chance) {
        chance = Math.abs(chance);
        if (chance > 100) chance = 100;
        return random.nextInt(100) + 1 <= chance;
    }

    public static boolean accurateChance(Random random, float chance) {
        chance = Math.abs(chance);
        if (chance > 100) chance = 100;
        return random.nextInt(1000) + 1 <= chance * 10;
    }

    public static boolean ultraPreciseChance(Random random, float chance) {
        chance = Math.abs(chance);
        if (chance > 100) chance = 100;
        return random.nextInt(10000) + 1 <= chance * 100;
    }

    public static long normalPercentFrom(Random random, long fromNumber, int percent, int maxDeviationPercent) {
        percent = Math.abs(percent);
        if (percent > 100) percent = 100;
        percent += random.nextInt(maxDeviationPercent * 2) + 1 - maxDeviationPercent;
        return percent * fromNumber / 100;
    }

    public static int normalPercentFrom(Random random, int fromNumber, int percent, int maxDeviationPercent) {
        percent = Math.abs(percent);
        if (percent > 100) percent = 100;
        percent += random.nextInt(maxDeviationPercent * 2) + 1 - maxDeviationPercent;
        return percent * fromNumber / 100;
    }

    public static double normalPercentFrom(Random random, double fromNumber, int percent, int maxDeviationPercent) {
        percent = Math.abs(percent);
        if (percent > 100) percent = 100;
        percent += random.nextInt(maxDeviationPercent * 2) + 1 - maxDeviationPercent;
        return percent * fromNumber / 100;
    }

    public static float normalPercentFrom(Random random, float fromNumber, int percent, int maxDeviationPercent) {
        percent = Math.abs(percent);
        if (percent > 100) percent = 100;
        percent += random.nextInt(maxDeviationPercent * 2) + 1 - maxDeviationPercent;
        return percent * fromNumber / 100;
    }

    public static long accuratePercentFrom(Random random, long fromNumber, float percent, float maxDeviationPercent) {
        percent = Math.abs(percent);
        if (percent > 100) percent = 100;
        int maxDeviationPercentI = (int) (maxDeviationPercent * 10);
        percent += (random.nextInt(maxDeviationPercentI * 2) + 1 - maxDeviationPercentI) / 10f;
        return (long) (percent * fromNumber / 100);
    }

    public static int accuratePercentFrom(Random random, int fromNumber, float percent, float maxDeviationPercent) {
        percent = Math.abs(percent);
        if (percent > 100) percent = 100;
        int maxDeviationPercentI = (int) (maxDeviationPercent * 10);
        percent += (random.nextInt(maxDeviationPercentI * 2) + 1 - maxDeviationPercentI) / 10f;
        return (int) (percent * fromNumber / 100);
    }

    public static double accuratePercentFrom(Random random, double fromNumber, float percent, float maxDeviationPercent) {
        percent = Math.abs(percent);
        if (percent > 100) percent = 100;
        int maxDeviationPercentI = (int) (maxDeviationPercent * 10);
        percent += (random.nextInt(maxDeviationPercentI * 2) + 1 - maxDeviationPercentI) / 10f;
        return percent * fromNumber / 100;
    }

    public static float accuratePercentFrom(Random random, float fromNumber, float percent, float maxDeviationPercent) {
        percent = Math.abs(percent);
        if (percent > 100) percent = 100;
        int maxDeviationPercentI = (int) (maxDeviationPercent * 10);
        percent += (random.nextInt(maxDeviationPercentI * 2) + 1 - maxDeviationPercentI) / 10f;
        return percent * fromNumber / 100;
    }

    public static long ultraPrecisePercentFrom(Random random, long fromNumber, float percent, float maxDeviationPercent) {
        percent = Math.abs(percent);
        if (percent > 100) percent = 100;
        int maxDeviationPercentI = (int) (maxDeviationPercent * 100);
        percent += (random.nextInt(maxDeviationPercentI * 2) + 1 - maxDeviationPercentI) / 100f;
        return (long) (percent * fromNumber / 100);
    }

    public static int ultraPrecisePercentFrom(Random random, int fromNumber, float percent, float maxDeviationPercent) {
        percent = Math.abs(percent);
        if (percent > 100) percent = 100;
        int maxDeviationPercentI = (int) (maxDeviationPercent * 100);
        percent += (random.nextInt(maxDeviationPercentI * 2) + 1 - maxDeviationPercentI) / 100f;
        return (int) (percent * fromNumber / 100);
    }

    public static double ultraPrecisePercentFrom(Random random, double fromNumber, float percent, float maxDeviationPercent) {
        percent = Math.abs(percent);
        if (percent > 100) percent = 100;
        int maxDeviationPercentI = (int) (maxDeviationPercent * 100);
        percent += (random.nextInt(maxDeviationPercentI * 2) + 1 - maxDeviationPercentI) / 100f;
        return percent * fromNumber / 100;
    }

    public static float ultraPrecisePercentFrom(Random random, float fromNumber, float percent, float maxDeviationPercent) {
        percent = Math.abs(percent);
        if (percent > 100) percent = 100;
        int maxDeviationPercentI = (int) (maxDeviationPercent * 100);
        percent += (random.nextInt(maxDeviationPercentI * 2) + 1 - maxDeviationPercentI) / 100f;
        return percent * fromNumber / 100;
    }

    private MouseRandom() { throw new UnsupportedOperationException("Cannot create utility class"); }
}