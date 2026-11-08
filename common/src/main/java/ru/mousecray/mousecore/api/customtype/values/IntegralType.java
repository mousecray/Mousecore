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

public final class IntegralType extends NumberType<Long> {
    public static final IntegralType MIN  = new IntegralType(Long.MIN_VALUE);
    public static final IntegralType MAX  = new IntegralType(Long.MAX_VALUE);
    public static final IntegralType NULL = new IntegralType(0L);

    static {
        storage.put(IntegralType.class, str -> {
            str = MouseStrings.trimWith(str, true, '\t');
            if (str == null || str.isEmpty()) throw new ValueFormatException();
            Double v = MouseNumbers.tryParseDouble(str);
            if (v != null) return create(v.longValue());
            throw new ValueFormatException();
        });
    }

    private IntegralType(long value) { super(value); }

    public static IntegralType create(long value) {
        if (MIN.asNumber() >= value) return MIN;
        else if (MAX.asNumber() <= value) return MAX;
        else if (value == 0) return NULL;
        else return new IntegralType(value);
    }

    public static IntegralType calcMedium(@Nonnull IntegralType min, @Nonnull IntegralType max) {
        return create((long) (min.value + max.value / 2D));
    }

    public static IntegralType calcLow(@Nonnull IntegralType min, @Nonnull IntegralType max) {
        return create((long) ((min.value + (min.value + max.value) / 2D) / 2D));
    }

    public static IntegralType calcHigh(@Nonnull IntegralType min, @Nonnull IntegralType max) {
        return create((long) ((max.value + (min.value + max.value) / 2D) / 2D));
    }

    @Nonnull @Override public Long asNumber() { return value; }

    @SuppressWarnings("unchecked") @Nonnull @Override
    public IntegralType createType(@Nonnull Number value) {
        return create(value.longValue());
    }
}