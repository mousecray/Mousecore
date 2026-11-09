/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.utils;

import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import java.util.Arrays;

public final class MouseMath {
    @Nullable
    public static int[] distributeNumber(int total, int numGroups) {
        if (numGroups <= 0 || total <= 0) return null;

        if (total <= numGroups) {
            int[] result = new int[(int) total];
            Arrays.fill(result, 1);
            return result;
        }

        int[] result = new int[numGroups];

        int baseSize = total / numGroups;

        for (int i = 0; i < numGroups; i++) {
            if (i == numGroups - 1) result[i] = baseSize + total % numGroups;
            else result[i] = baseSize;
        }

        return result;
    }

    @Nullable
    public static long[] distributeNumber(long total, int numGroups) {
        if (numGroups <= 0 || total <= 0) return null;

        if (total <= numGroups) {
            long[] result = new long[(int) total];
            Arrays.fill(result, 1);
            return result;
        }

        long[] result = new long[numGroups];

        long baseSize = total / numGroups;

        for (int i = 0; i < numGroups; i++) {
            if (i == numGroups - 1) result[i] = baseSize + total % numGroups;
            else result[i] = baseSize;
        }

        return result;
    }

    @Nullable
    public static int[] toGroupWithMax(long total, int max) {
        if (max <= 0 || total <= 0) return null;

        if (total <= max) {
            int[] result = new int[1];
            result[0] = (int) total;
            return result;
        }

        int   groupCount = (int) Math.ceil((double) total / max);
        int[] result     = new int[groupCount];
        long  remain     = total;

        for (int i = 0; i < groupCount; ++i) {
            int groupSize = (int) Math.min(remain, max); //Current group size
            result[i] = groupSize;
            remain -= groupSize; //Reduce remain
        }

        return result;
    }

    public static float normalize(float value, float min, float max) {
        if (max == min) return 0.0f;
        float normalized = (value - min) / (max - min);
        return Mth.clamp(normalized, 0.0f, 1.0f);  // ConstraintImpl [0, 1]
    }

    private MouseMath() { throw new UnsupportedOperationException("Cannot create utility class"); }
}