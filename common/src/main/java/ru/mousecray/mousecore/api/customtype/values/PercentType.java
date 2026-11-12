/*******************************************************************************
 * Copyright © 2026 mousecray
 * Licensed under the GNU Lesser General Public License, Version 3.0
 ******************************************************************************/

package ru.mousecray.mousecore.api.customtype.values;

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
    public static final PercentType NULL = new PercentType(0D);

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
        if (MIN.asNumber() >= value) return MIN;
        else if (MAX.asNumber() <= value) return MAX;
        else if (value == 0) return NULL;
        else return new PercentType(value);
    }

    @Override public IValType getValType()                                               { return ValType.PERCENT; }

    public static PercentType calcMedium(PercentType min, PercentType max)               { return create(min.value + max.value / 2D); }
    public static PercentType calcLow(PercentType min, PercentType max)                  { return create((min.value + (min.value + max.value) / 2D) / 2D); }
    public static PercentType calcHigh(PercentType min, PercentType max)                 { return create((max.value + (min.value + max.value) / 2D) / 2D); }

    @Override public Double asNumber()                                                   { return value; }
    @Override public String toString()                                                   { return super.toString() + '%'; }

    @SuppressWarnings("unchecked") @Override public PercentType createType(Number value) { return create(value.doubleValue()); }
}
