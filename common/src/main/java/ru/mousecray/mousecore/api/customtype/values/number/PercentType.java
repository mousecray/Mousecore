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

import javax.annotation.ParametersAreNonnullByDefault;

import static ru.mousecray.mousecore.api.utils.MouseNumbers.tryParseDouble;
import static ru.mousecray.mousecore.api.utils.MouseStrings.subAndTrimWithTabs;

@ParametersAreNonnullByDefault
@MethodReturnsNonnullByDefault
public final class PercentType extends NumberType<Double> {
    public static final PercentType MIN  = new PercentType(0D);
    public static final PercentType MAX  = new PercentType(100D);
    public static final PercentType NULL = MIN;

    static {
        storage.put(PercentType.class, str -> {
            str = MouseStrings.trimWith(str, true, '\t');
            if (str == null || str.isEmpty()) throw new ValueFormatException();
            Double v = tryParseDouble(subAndTrimWithTabs(str, null, "%"));
            if (v != null) return create(v);
            throw new ValueFormatException();
        });
    }

    private PercentType(double value) { super(value); }

    public static PercentType create(double value) {
        if (Double.isNaN(value) || value <= 0D) return MIN;
        else if (value >= 100D) return MAX;
        else return new PercentType(value);
    }

    @Override public IValType getValType()                                               { return ValType.PERCENT; }

    public static PercentType calcMedium(PercentType min, PercentType max)               { return create(min.value * 0.5D + max.value * 0.5D); }
    public static PercentType calcLow(PercentType min, PercentType max)                  { return create(min.value * 0.75D + max.value * 0.25D); }
    public static PercentType calcHigh(PercentType min, PercentType max)                 { return create(min.value * 0.25D + max.value * 0.75D); }

    @Override public Double asNumber()                                                   { return value; }
    @Override public String toString()                                                   { return super.toString() + '%'; }

    @SuppressWarnings("unchecked") @Override public PercentType createType(Number value) { return create(value.doubleValue()); }
}
