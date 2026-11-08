/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype.values;

import ru.mousecray.mousecore.api.customtype.NumberType;
import ru.mousecray.mousecore.api.error.ValueFormatException;
import ru.mousecray.mousecore.api.utils.MouseNumbers;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.Nonnull;

public final class DecimalType extends NumberType<Double> {
    public static final DecimalType MIN  = new DecimalType(Double.MIN_VALUE);
    public static final DecimalType MAX  = new DecimalType(Double.MAX_VALUE);
    public static final DecimalType NULL = new DecimalType(0D);

    static {
        storage.put(DecimalType.class, str -> {
            str = MouseStrings.trimWith(str, true, '\t');
            if (str == null || str.isEmpty()) throw new ValueFormatException();
            Double v = MouseNumbers.tryParseDouble(str);
            if (v != null) return create(v);
            throw new ValueFormatException();
        });
    }

    private DecimalType(double value) { super(value); }

    public static DecimalType create(double value) {
        if (MIN.asNumber() >= value) return MIN;
        else if (MAX.asNumber() <= value) return MAX;
        else if (value == 0) return NULL;
        else return new DecimalType(value);
    }

    public static DecimalType calcMedium(@Nonnull DecimalType min, @Nonnull DecimalType max) {
        return create(min.value + max.value / 2D);
    }

    public static DecimalType calcLow(@Nonnull DecimalType min, @Nonnull DecimalType max) {
        return create((min.value + (min.value + max.value) / 2D) / 2D);
    }

    public static DecimalType calcHigh(@Nonnull DecimalType min, @Nonnull DecimalType max) {
        return create((max.value + (min.value + max.value) / 2D) / 2D);
    }

    @Nonnull @Override public Double asNumber() { return value; }

    @SuppressWarnings("unchecked") @Nonnull @Override
    public DecimalType createType(@Nonnull Number value) {
        return create(value.doubleValue());
    }
}