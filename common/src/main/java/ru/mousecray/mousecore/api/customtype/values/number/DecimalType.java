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
import ru.mousecray.mousecore.api.utils.MouseNumbers;
import ru.mousecray.mousecore.api.utils.MouseStrings;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodReturnsNonnullByDefault
@ParametersAreNonnullByDefault
public final class DecimalType extends NumberType<Double> {
    public static final DecimalType MIN  = new DecimalType(-Double.MAX_VALUE);
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
        if (value == 0D) return NULL;
        else if (value <= -Double.MAX_VALUE) return MIN;
        else if (value >= Double.MAX_VALUE) return MAX;
        else return new DecimalType(value);
    }

    @Override public IValType getValType()                                               { return ValType.DECIMAL; }

    public static DecimalType calcMedium(DecimalType min, DecimalType max)               { return create(min.value * 0.5D + max.value * 0.5D); }
    public static DecimalType calcLow(DecimalType min, DecimalType max)                  { return create(min.value * 0.75D + max.value * 0.25D); }
    public static DecimalType calcHigh(DecimalType min, DecimalType max)                 { return create(min.value * 0.25D + max.value * 0.75D); }

    @Override public Double asNumber()                                                   { return value; }

    @SuppressWarnings("unchecked") @Override public DecimalType createType(Number value) { return create(value.doubleValue()); }
}